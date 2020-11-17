package com.hungwen.cloudmall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.client.utils.StringUtils;
import com.hungwen.cloudmall.ware.entity.WareOrderTaskDetailEntity;
import com.hungwen.cloudmall.ware.entity.WareOrderTaskEntity;
import com.hungwen.cloudmall.ware.feign.OrderFeignService;
import com.hungwen.cloudmall.ware.feign.ProductFeignService;
import com.hungwen.cloudmall.ware.service.WareOrderTaskDetailService;
import com.hungwen.cloudmall.ware.service.WareOrderTaskService;
import com.hungwen.cloudmall.ware.vo.OrderItemVo;
import com.hungwen.cloudmall.ware.vo.OrderVo;
import com.hungwen.cloudmall.ware.vo.SkuHasStockVo;
import com.hungwen.cloudmall.ware.vo.WareSkuLockVo;
import com.hungwen.common.exception.NoStockException;
import com.hungwen.common.to.mq.OrderTo;
import com.hungwen.common.to.mq.StockDetailTo;
import com.hungwen.common.to.mq.StockLockedTo;
import com.hungwen.common.utils.R;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.Data;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.common.utils.Query;

import com.hungwen.cloudmall.ware.dao.WareSkuDao;
import com.hungwen.cloudmall.ware.entity.WareSkuEntity;
import com.hungwen.cloudmall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;

@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    private WareOrderTaskService wareOrderTaskService;
    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private OrderFeignService orderFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String wareId = (String)params.get("wareId");
        String skuId=(String)params.get("skuId");

        if(StringUtils.isNotEmpty(wareId)){
            queryWrapper.eq("ware_id", wareId);
        }

        if(StringUtils.isNotEmpty(skuId)){
            queryWrapper.eq("sku_id", skuId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {

        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(
                new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));

        if(wareSkuEntities == null || wareSkuEntities.size() ==0 ){
            //新增
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            // 遠程查詢 sku 的 name，若失敗無需回滾
            try {
                R info = productFeignService.info(skuId);
                if(info.getCode() == 0){
                    Map<String, Object> data = (Map<String,Object>)info.get("skuInfo");
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            } catch (Exception e) {

            }
            wareSkuDao.insert(wareSkuEntity);
        }else{
            // 插入
            wareSkuDao.addStock(skuId,wareId,skuNum);
        }
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> skuHasStockVos = skuIds.stream().map(item -> {
            Long count = this.baseMapper.getSkuStock(item);
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            skuHasStockVo.setSkuId(item);
            skuHasStockVo.setHasStock(count == null ? false : count > 0);
            return skuHasStockVo;
        }).collect(Collectors.toList());
        return skuHasStockVos;
    }

    /**
     * 為某個訂單鎖定庫存
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean orderLockStock(WareSkuLockVo vo) {
        /**
         * 保存庫存工作單詳情 資料
         * 追溯
         */
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(vo.getOrderSn());
        wareOrderTaskEntity.setCreateTime(new Date());
        wareOrderTaskService.save(wareOrderTaskEntity);
        // 1. 按照下單的收貨地址，找到一個就近倉庫，鎖定庫存
        // 2. 找到每個商品在哪個倉庫都有庫存
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map((item) -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            // 查詢這個商品在哪個倉庫有庫存
            List<Long> wareIdList = wareSkuDao.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareIdList);
            return stock;
        }).collect(Collectors.toList());
        // 2. 鎖定庫存
        for (SkuWareHasStock hasStock : collect) {
            boolean skuStocked = false;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();

            if (org.springframework.util.StringUtils.isEmpty(wareIds)) {
                // 沒有任何倉庫有這個商品的庫存
                throw new NoStockException(skuId);
            }
            // 1. 如果每一個商品都鎖定成功，將當前商品鎖定了幾件的工作單記錄發給 MQ
            // 2. 鎖定失敗。前面保存的工作單 資料都回滾了。發送出去的消息，即使要解鎖庫存，由於在資料庫查不到指定的 id，所有就不用解鎖
            for (Long wareId : wareIds) {
                // 鎖定成功就返回 1，失敗就返回 0
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, hasStock.getNum());
                if (count == 1) {
                    skuStocked = true;
                    WareOrderTaskDetailEntity taskDetailEntity = WareOrderTaskDetailEntity.builder()
                            .skuId(skuId)
                            .skuName("")
                            .skuNum(hasStock.getNum())
                            .taskId(wareOrderTaskEntity.getId())
                            .wareId(wareId)
                            .lockStatus(1)
                            .build();
                    wareOrderTaskDetailService.save(taskDetailEntity);
                    // 告訴 MQ 庫存鎖定成功
                    StockLockedTo lockedTo = new StockLockedTo();
                    lockedTo.setId(wareOrderTaskEntity.getId());
                    StockDetailTo detailTo = new StockDetailTo();
                    BeanUtils.copyProperties(taskDetailEntity, detailTo);
                    lockedTo.setStockDetail(detailTo);
                    rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked" , lockedTo);
                    break;
                } else {
                    // 當前倉庫鎖失敗，重試下一個倉庫
                }
            }
            if (skuStocked == false) {
                // 當前商品所有倉庫都沒有鎖住
                throw new NoStockException(skuId);
            }
        }
        // 3. 肯定全部都是鎖定成功的
        return true;
    }

    @Override
    public void unlockStock(StockLockedTo to) {
        // 庫存工作單的 id
        System.out.println("收到解鎖庫存的消息");
        StockDetailTo detail = to.getStockDetail();
        Long detailId = detail.getId();
        /**
         * 解鎖
         * 1、查詢資料庫關於這個訂單鎖定庫存資料
         *   有：證明庫存鎖定成功了
         *      解鎖：訂單狀況
         *          1、沒有這個訂單，必須解鎖庫存
         *          2、有這個訂單，不一定解鎖庫存
         *              訂單狀態：已取消：解鎖庫存
         *                      已支付：不能解鎖庫存
         */
        WareOrderTaskDetailEntity taskDetailInfo = wareOrderTaskDetailService.getById(detailId);
        if (taskDetailInfo != null) {
            // 查出 wms_ware_order_task 工作單的資料
            Long id = to.getId();
            WareOrderTaskEntity orderTaskInfo = wareOrderTaskService.getById(id);
            // 獲取訂單號查詢訂單狀態
            String orderSn = orderTaskInfo.getOrderSn();
            // 遠程查詢訂單資料
            R orderData = orderFeignService.getOrderStatus(orderSn);
            if (orderData.getCode() == 0) {
                // 訂單資料返回成功
                OrderVo orderInfo = orderData.getData("data", new TypeReference<OrderVo>() {});
                // 判斷訂單狀態是否已取消或者支付或者訂單不存在
                if (orderInfo == null || orderInfo.getStatus() == 4) {
                    // 訂單已被取消，才能解鎖庫存
                    if (taskDetailInfo.getLockStatus() == 1) {
                        // 當前庫存工作單詳情狀態 1，已鎖定，但是未解鎖才可以解鎖
                        unLockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detailId);
                    }
                }
            } else {
                // 消息拒絕以後重新放在隊列裡面，讓別人繼續消費解鎖
                // 遠程調用服務失敗
                throw new RuntimeException("遠程調用服務失敗");
            }
        } else {
            // 無需解鎖
        }
    }
    
    /**
     * 防止訂單服務卡頓，導致訂單狀態消息一直改不了，庫存優先到期，查訂單狀態新建，什麼都不處理
     * 導致卡頓的訂單，永遠都不能解鎖庫存
     * @param orderTo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unlockStock(OrderTo orderTo) {
        String orderSn = orderTo.getOrderSn();
        // 查一下最新的庫存解鎖狀態，防止重復解鎖庫存
        WareOrderTaskEntity orderTaskEntity = wareOrderTaskService.getOrderTaskByOrderSn(orderSn);
        // 按照工作單的id找到所有 沒有解鎖的庫存，進行解鎖
        Long id = orderTaskEntity.getId();
        List<WareOrderTaskDetailEntity> list = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>()
                .eq("task_id", id).eq("lock_status", 1));
        for (WareOrderTaskDetailEntity taskDetailEntity : list) {
            unLockStock(taskDetailEntity.getSkuId(), taskDetailEntity.getWareId(), taskDetailEntity.getSkuNum(), taskDetailEntity.getId());
        }
    }

    /**
     * 解鎖庫存的方法
     * @param skuId
     * @param wareId
     * @param num
     * @param taskDetailId
     */
    public void unLockStock(Long skuId, Long wareId, Integer num, Long taskDetailId) {
        // 庫存解鎖
        wareSkuDao.unLockStock(skuId, wareId, num);
        // 更新工作單的狀態
        WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity();
        taskDetailEntity.setId(taskDetailId);
        // 變為已解鎖
        taskDetailEntity.setLockStatus(2);
        wareOrderTaskDetailService.updateById(taskDetailEntity);
    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private Integer num;
        private List<Long> wareId;
    }

}