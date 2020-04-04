import redis.clients.jedis.Jedis;

/**
 * @author star
 * @create 2019-04-03 12:00
 */
public class Test {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("192.168.88.59",6379);

        System.out.println(jedis.ping());
    }
}
