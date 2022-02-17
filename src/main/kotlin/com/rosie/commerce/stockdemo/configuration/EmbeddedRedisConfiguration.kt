import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import redis.embedded.RedisServer
import javax.annotation.PreDestroy

@Configuration
class EmbeddedRedisConfiguration(
    @Value("\${redis.port}") private val port: Int,
) {
    private val redisServer: RedisServer = RedisServer(port)

    init {
        redisServer.start()
    }

    @PreDestroy
    fun stopRedis() {
        redisServer.stop()
    }
}