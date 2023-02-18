package com.iw.gateway.service.impl;

import com.google.code.kaptcha.Producer;
import com.iw.common.core.constant.Constants;
import com.iw.common.core.constant.CacheConstants;
import com.iw.common.core.domain.R;
import com.iw.common.core.domain.redis.RedisObject;
import com.iw.common.core.exception.CaptchaException;
import com.iw.common.core.utils.StringUtils;
import com.iw.common.core.utils.sign.Base64;
import com.iw.common.core.utils.uuid.IdUtils;
import com.iw.common.core.domain.AjaxResult;
import com.iw.gateway.config.properties.CaptchaProperties;
import com.iw.gateway.service.TestClient;
import com.iw.gateway.service.ValidateCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 验证码实现处理
 *
 * @author ruoyi
 */
@Service
public class ValidateCodeServiceImpl implements ValidateCodeService {
    @Autowired
    private Producer captchaProducer;
    @Autowired
    private Producer captchaProducerMath;
    @Autowired
    private CaptchaProperties captchaProperties;

    @Autowired
    private TestClient testClient;

    /**
     * 生成验证码
     */
    @Override
    public AjaxResult createCaptcha() throws IOException, CaptchaException {
        AjaxResult ajax = AjaxResult.success();
        boolean captchaEnabled = captchaProperties.getEnabled();
        ajax.put("captchaEnabled", captchaEnabled);
        if (!captchaEnabled) {
            return ajax;
        }

        // 保存验证码信息
        String uuid = IdUtils.simpleUUID();
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;

        String capStr = null, code = null;
        BufferedImage image = null;

        String captchaType = captchaProperties.getType();
        // 生成验证码
        if ("math".equals(captchaType)) {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        }
        else if ("char".equals(captchaType)) {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }
        RedisObject redisObject = new RedisObject();
        redisObject.setKey(verifyKey);
        redisObject.setVal(code);
        redisObject.setTime(Constants.CAPTCHA_EXPIRATION);
        redisObject.setTimeUnit(TimeUnit.MINUTES);
        Future<R<Boolean>> rFuture = testClient.setVal(redisObject);
        try {
            rFuture.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        }
        catch (IOException e) {
            return AjaxResult.error(e.getMessage());
        }
        ajax.put("uuid", uuid);
        ajax.put("img", Base64.encode(os.toByteArray()));
        return ajax;
    }

    /**
     * 校验验证码
     */
    @Override
    public void checkCaptcha(String code, String uuid) throws CaptchaException {
        if (StringUtils.isEmpty(code)) {
            throw new CaptchaException("验证码不能为空");
        }
        if (StringUtils.isEmpty(uuid)) {
            throw new CaptchaException("验证码已失效");
        }
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;
        String captcha = "";
        try {
            Future<R<Object>> rFuture = testClient.getVal(verifyKey);
            try {
                R<Object> booleanR = rFuture.get();
                captcha = booleanR.getData().toString();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            //  captcha = (String) redisService.getVal(verifyKey).getData();
        } catch (Exception e) {

        }
        // redisService.getCacheObject(verifyKey);
        // redisService.deleteObject(verifyKey);
        if (!code.equalsIgnoreCase(captcha)) {
            throw new CaptchaException("验证码错误");
        }
    }
}
