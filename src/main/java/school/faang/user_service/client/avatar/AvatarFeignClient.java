package school.faang.user_service.client.avatar;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "avatarClient", url = "${dicebear.endpoint}")
public interface AvatarFeignClient {

    @GetMapping(value = "/9.x/adventurer/png", params = {"seed", "size"})
    ByteArrayResource getAvatar(@RequestParam("seed") String seed, @RequestParam("size") int size);
}
