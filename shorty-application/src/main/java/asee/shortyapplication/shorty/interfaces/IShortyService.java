package asee.shortyapplication.shorty.interfaces;

import asee.shortyapplication.shorty.dto.ResolvedHashResponse;
import asee.shortycore.exceptions.ShortyException;

import java.util.Map;

public interface IShortyService {
    String shortenTheUrl(String url, int redirectionType, String accountId) throws ShortyException;

    ResolvedHashResponse resolveTheHashedUrl(String hashedUrl, String accountId) throws ShortyException;

    Map<String, Integer> getUsersShortyStatistics(String accountId);

    void throwIfIncorrectRedirectionType(Integer redirectionType) throws ShortyException;
}
