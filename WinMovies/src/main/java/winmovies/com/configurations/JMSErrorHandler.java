package winmovies.com.configurations;

import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

@Component
public class JMSErrorHandler implements ErrorHandler {

    @Override
    public void handleError(Throwable t) {
        System.out.println("An error occurred in message processing " + t.getMessage());
    }
}
