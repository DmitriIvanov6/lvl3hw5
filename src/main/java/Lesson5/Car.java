package Lesson5;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Car implements Runnable {
    private static int CARS_COUNT;

    static {
        CARS_COUNT = 0;
    }

    private Race race;
    private int speed;
    private String name;

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public Car(Race race, int speed) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }


    final static CountDownLatch cd1 = new CountDownLatch(CARS_COUNT);
    final static CountDownLatch cd2 = new CountDownLatch(CARS_COUNT);
    final static CountDownLatch cd3 = new CountDownLatch(CARS_COUNT - 1);
    final static Lock finisher = new ReentrantLock();


    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int) (Math.random() * 800));
            System.out.println(this.name + " готов");
            cd1.countDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            cd1.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }
        if (finisher.tryLock()) {
            try {
                System.out.println("Победитель " + this.name);
            } finally {
                try {
                    cd3.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finisher.unlock();
            }
        }
        cd3.countDown();
        cd2.countDown();
    }
}
