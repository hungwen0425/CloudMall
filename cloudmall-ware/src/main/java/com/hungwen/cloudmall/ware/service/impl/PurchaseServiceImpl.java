package com.hungwen.cloudmall.ware.service.impl;

import com.hungwen.cloudmall.ware.entity.PurchaseDetailEntity;
import com.hungwen.cloudmall.ware.service.PurchaseDetailService;
import com.hungwen.cloudmall.ware.service.WareSkuService;
import com.hungwen.cloudmall.ware.vo.MergeVo;
import com.hungwen.cloudmall.ware.vo.PurchaseFinishItem;
import com.hungwen.cloudmall.ware.vo.PurchaseFinishVo;
import com.hungwen.common.constant.WareConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.common.utils.Query;

import com.hungwen.cloudmall.ware.dao.PurchaseDao;
import com.hungwen.cloudmall.ware.entity.PurchaseEntity;
import com.hungwen.cloudmall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;
    @Autowired
    WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void received(List<Long> ids) {

        //1. 確認當前採購單是新建或已分配狀態
        List<PurchaseEntity> purchaseEntities = ids.stream().map(item -> {
            PurchaseEntity byId = this.getById(item);
            return byId;
        }).filter(item -> {
            //過濾出新建或已分配的採購單
            boolean flag=(item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode()) ||
                    (item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode());
            return flag;
        }).map(item -> {
            //將採購單狀態設定為已接收，並且只更新狀態和時間字段
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(item.getId());
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVED.getCode());
            purchaseEntity.setUpdateTime(new Date());
            item = null;//GC
            return  purchaseEntity;
        }).collect(Collectors.toList());

        //2. 改變採購單的狀態
        this.updateBatchById(purchaseEntities);

        //3. 改變採購項的狀態
        purchaseEntities.stream().forEach(item -> {
            List<PurchaseDetailEntity> entities = purchaseDetailService.listDetailByPurchaseId(item.getId());
            //只更新狀態字段即可
            List<PurchaseDetailEntity> collect = entities.stream().map(detailEntity -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(detailEntity.getId());
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                detailEntity=null;//GC
                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect);
        });
    }

    @Override
    public PageUtils queryPageUnreceived(Map<String, Object> params) {
        // 取得採購單中狀態為 0 或 1 的採購單
        QueryWrapper<PurchaseEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",0).or().eq("status",1);
        IPage<PurchaseEntity> page = this.page(new Query<PurchaseEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    /**
     *
     * @param mergeVo
     */
    @Override
    @Transactional
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        // 如果 purchaseId 不存在，保存 PurchaseEntity 並設定狀態和時間戳
        // 保存到 wms_purchase
        if(purchaseId == null){
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);

            purchaseId=purchaseEntity.getId();

        }else{
            // 如果採購單的狀態不是新建或已分配，則返回
            PurchaseEntity purchaseEntity = this.baseMapper.selectById(mergeVo.getPurchaseId());
            boolean flag=(purchaseEntity.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode()) ||
                    (purchaseEntity.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode());
            if(!flag){
                return;
            }
        }

        //更新 wms_purchase_detail
        Long finalPurchaseId =purchaseId;
        List<Long> items = mergeVo.getItems();
        List<PurchaseDetailEntity> collect = items.stream().map(i -> {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(i);
            detailEntity.setPurchaseId(finalPurchaseId);
            detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return detailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

    /**
     * 更新商品的採購狀態，庫存狀態和更新採購單的狀態
     * （1）如果商品採購成功，則更新採購狀態為已完成，更新庫存值，更新採購單已經完成
     * （2）如果商品採購失敗，則更新採購狀態為採購失敗，更新採購單狀態位失敗
     * @param finishVo
     */
    @Transactional
    @Override
    public void done(PurchaseFinishVo finishVo) {
        // 1. 改變採購項的狀態
        List<PurchaseFinishItem> finishVoItems = finishVo.getItems();
        AtomicBoolean flag= new AtomicBoolean(true);
        // 設定採購項的狀態
        List<PurchaseDetailEntity> detailEntities = finishVoItems.stream().map(item -> {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(item.getItemId());
            // 是否採購失敗
            boolean failFlag = item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode();
            if (failFlag) {
                detailEntity.setStatus(item.getStatus());
                flag.set(false);
            } else {
                // 採購成功
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                // 2. 將成功的採購進行入庫
                // 採購成功的商品 id
                Long itemId = item.getItemId();
                //取得sku_id，sku_num和ware_id
                PurchaseDetailEntity byId = purchaseDetailService.getById(itemId);
                wareSkuService.addStock(byId.getSkuId(),byId.getWareId(),byId.getSkuNum());
            }
            item = null;//GC
            return detailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(detailEntities);

        //3. 改變採購單的狀態
        Long id = finishVo.getId();
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag.get() ? WareConstant.PurchaseStatusEnum.FINISH.getCode():
                WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());

        this.updateById(purchaseEntity);
    }
}