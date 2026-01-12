package course.concurrency.m3_shared.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> latestBid = new AtomicReference<>(new Bid(0L, 0L, Long.MIN_VALUE));

    public boolean propose(Bid bid) {
        Bid current;
        do {
            current = latestBid.get();
            if (bid.getPrice() <= current.getPrice()) {
                return false;
            }
        } while (!latestBid.compareAndSet(current, bid));
        notifier.sendOutdatedMessage(current);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
