import com.google.inject.AbstractModule;
import util.DataInit;

public class EagerLoaderModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DataInit.class).asEagerSingleton();
    }
}
