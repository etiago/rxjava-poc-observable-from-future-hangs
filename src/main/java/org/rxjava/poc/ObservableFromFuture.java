package org.rxjava.poc;


import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ObservableFromFuture {
    public static void main(String[] args) {
        HttpTransport httpTransport = new NetHttpTransport();
        HttpRequestFactory httpRequestFactory = httpTransport.createRequestFactory();

        try {
            HttpRequest httpRequest = httpRequestFactory.buildGetRequest(new GenericUrl("https://api.github.com/users/etiago"));

            Maybe<String> maybeString =
                    Single
                            .fromFuture(httpRequest.executeAsync())
                            .map(httpResponse -> {
                                InputStreamReader inputStreamReader =
                                        new InputStreamReader(httpResponse.getContent());
                                BufferedReader bufferedReader =
                                        new BufferedReader(inputStreamReader);

                                return Observable.fromIterable(bufferedReader.lines()::iterator).reduce(String::concat);
                            })
                            .blockingGet();

            System.out.println(maybeString.blockingGet());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}