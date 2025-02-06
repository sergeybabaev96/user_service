package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import school.faang.user_service.client.config.DiceBearFeignConfig;

@FeignClient(name = "diceBearClient", url = "https://api.dicebear.com/9.x", configuration = DiceBearFeignConfig.class)
public interface DiceBearClient {
    @GetMapping("/{style}/png")
    byte[] getAvatar(@PathVariable("style") String style,
                     @RequestParam("seed") String seed,
                     @RequestParam("format") String format);
}