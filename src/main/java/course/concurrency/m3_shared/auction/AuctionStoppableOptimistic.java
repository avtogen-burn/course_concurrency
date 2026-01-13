package course.concurrency.m3_shared.auction;

import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private Notifier notifier;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicMarkableReference<Bid> latestBid =
            new AtomicMarkableReference<>(new Bid(0L, 0L, Long.MIN_VALUE), false);

    public boolean propose(Bid bid) {
        Bid current;
        do {
            if (latestBid.isMarked())
                return false;
            current = latestBid.getReference();
            if (current != null && bid.getPrice() <= current.getPrice()) {
                return false;
            }
        } while (!latestBid.compareAndSet(current, bid, false, false));
        notifier.sendOutdatedMessage(current);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.getReference();
    }

    public Bid stopAuction() {
        Bid current;
        do {
            current = latestBid.getReference();
        } while (!latestBid.attemptMark(current, true));
        return current;
    }
}
