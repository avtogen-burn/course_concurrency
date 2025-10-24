package course.concurrency.m2_async.minPrice;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    private ExecutorService executor = Executors.newCachedThreadPool();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        List<CompletableFuture<Double>> tasks =
            shopIds.stream()
                .map(shopId ->
                    CompletableFuture
                            .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId) , executor)
                            .exceptionally(e -> Double.NaN)
                )
                .toList();

        CompletableFuture<Void> allTasks =
                CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new))
                        .orTimeout(2900, TimeUnit.MILLISECONDS);

        return allTasks.handle((t, ex) ->
            tasks.stream()
                .filter(CompletableFuture::isDone)
                .map(f -> {
                            try {
                                return f.join();
                            } catch (Exception e) {
                                return Double.NaN;
                            }
                        }
                )
                .filter(p -> !Double.isNaN(p) && !p.equals(0D))
                .reduce(Double::min)
                .orElse(Double.NaN)
        ).join();
    }
}
