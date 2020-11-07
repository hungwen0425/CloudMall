package com.hungwen.cloudmall.cart.controller;

import com.hungwen.cloudmall.cart.service.CartService;
import com.hungwen.cloudmall.cart.vo.CartItemVo;
import com.hungwen.cloudmall.cart.vo.CartVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-05 17:12
 **/

@Controller
public class CartController {

    @Resource
    private CartService cartService;

    /**
     * 獲取當前用戶的購物車商品項
     * @return
     */
    @GetMapping(value = "/currentUserCartItems")
    @ResponseBody
    public List<CartItemVo> getCurrentCartItems() {
        List<CartItemVo> cartItemVoList = cartService.getUserCartItems();
        return cartItemVoList;
    }

    /**
     * 去購物車頁面的請求
     * 瀏覽器有一個 cookie:user-key 標識用戶的身份，一個月過期
     * 如果第一次使用京東的購物車功能，都會給一個臨時的用戶身份:
     * 瀏覽器以後保存，每次訪問都會帶上這個 cookie；
     *
     * 登入：session 有
     * 沒登入：按照 cookie 裡面帶來 user-key 來做
     * 第一次，如果沒有臨時用戶，自動創建一個臨時用戶
     * @return
     */
    @GetMapping(value = "/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
        // 快速得到用戶資料：id, user-key
        // UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        CartVo cartVo = cartService.getCart();
        model.addAttribute("cart", cartVo);
        return "cartList";
    }

    /**
     * 添加商品到購物車
     * attributes.addFlashAttribute():將資料放在 session 中，可以在頁面中取出，但是只能取一次
     * attributes.addAttribute():將資料放在 url 後面
     * @return
     */
    @GetMapping(value = "/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes attributes)
            throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId, num);
        attributes.addAttribute("skuId", skuId);
        return "redirect:http://cart.cloudmall.com/addToCartSuccessPage.html";
    }

    /**
     * 跳轉到添加購物車成功頁面
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping(value = "/addToCartSuccessPage.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model) {
        // 重定向到成功頁面。再次查詢購物車資料即可
        CartItemVo cartItemVo = cartService.getCartItem(skuId);
        model.addAttribute("cartItem", cartItemVo);
        return "success";
    }

    /**
     * 商品是否選中
     * @param skuId
     * @param checked
     * @return
     */
    @GetMapping(value = "/checkItem")
    public String checkItem(@RequestParam(value = "skuId") Long skuId, @RequestParam(value = "checked") Integer checked) {
        cartService.checkItem(skuId, checked);
        return "redirect:http://cart.cloudmall.com/cart.html";
    }

    /**
     * 改變商品數量
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping(value = "/countItem")
    public String countItem(@RequestParam(value = "skuId") Long skuId, @RequestParam(value = "num") Integer num) {
        cartService.changeItemCount(skuId, num);
        return "redirect:http://cart.cloudmall.com/cart.html";
    }

    /**
     * 刪除商品資料
     * @param skuId
     * @return
     */
    @GetMapping(value = "/deleteItem")
    public String deleteItem(@RequestParam("skuId") Integer skuId) {
        cartService.deleteIdCartInfo(skuId);
        return "redirect:http://cart.cloudmall.com/cart.html";
    }
}
