package course.concurrency.m3_shared.auction;

import java.util.concurrent.locks.ReentrantLock;

public class AuctionPessimistic implements Auction {

    private Notifier notifier;

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = new Bid(0L, 0L, 0L);

    private final ReentrantLock lock = new ReentrantLock();

    public boolean propose(Bid bid) {
        if (bid.getPrice() > latestBid.getPrice()) {
            lock.lock();
            try {
                if (bid.getPrice() > latestBid.getPrice()) {
                    notifier.sendOutdatedMessage(latestBid);
                    latestBid = bid;
                    return true;
                }
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
