package codeforcescrawl;

import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;

import java.util.List;

public class EnqueueTasks {

    public static void main(String[] args) throws Exception {
        Database db = Factory.createDatabase();

        RedissonClient client = Factory.createRedissonClient();

        System.out.println("getting the tasks queue");
        RQueue<String> queue = client.getQueue("tasks");

        System.out.println("populating the queue");
        List<String> tasks = db.allUnscrapedTasks();

        for (String task : tasks) {
            queue.add(task);
            System.out.println("added " + task);
        }

        client.shutdown();
    }

}
