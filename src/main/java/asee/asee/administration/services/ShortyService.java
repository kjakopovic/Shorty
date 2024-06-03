package asee.asee.administration.services;

import asee.asee.administration.models.Shorty;
import asee.asee.administration.models.UserEntity;
import asee.asee.administration.models.UserShorty;
import asee.asee.administration.models.UserShortyId;
import asee.asee.administration.repositories.IShortyRepository;
import asee.asee.administration.repositories.IUserRepository;
import asee.asee.administration.repositories.IUserShortyRepository;
import asee.asee.administration.responseDtos.ResolvedHashResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ShortyService {

    private final PasswordEncoder passwordEncoder;
    private final IShortyRepository shortyRepository;
    private final IUserRepository userRepository;
    private final IUserShortyRepository userShortyRepository;

    @Autowired
    public ShortyService(PasswordEncoder passwordEncoder,
                         IShortyRepository shortyRepository,
                         IUserRepository userRepository,
                         IUserShortyRepository userShortyRepository) {
        this.passwordEncoder = passwordEncoder;
        this.shortyRepository = shortyRepository;
        this.userRepository = userRepository;
        this.userShortyRepository = userShortyRepository;
    }

    public String shortenTheUrl(String url, int redirectionType, String accountId) throws Exception {
        UserEntity loggedInUser = userRepository.findById(accountId)
                .orElseThrow();

        try {
            List<Shorty> shorty = shortyRepository.findShortiesByOriginalUrl(url);

            if (!shorty.isEmpty()){
                boolean isUserShortyExisting = userShortyRepository
                        .existsByUserEntityAccountIdAndShortyOriginalUrl(accountId, url);

                //ako konekcija za tog usera i shortija postoji ali je krivi redirection type
                // (jer jedan user ne moze imati vise redirection typeova na isti link)
                if (isUserShortyExisting && shorty.stream().anyMatch(s -> s.getRedirectionType() != redirectionType)){
                    throw new Exception("Već ste napravili taj link sa drugim redirection typeom!");
                //ako konekcija postoji i tocan je redirection type
                }else if (isUserShortyExisting && shorty.stream().anyMatch(s -> s.getRedirectionType() == redirectionType)){
                    Optional<Shorty> newShorty = shorty.stream()
                            .filter(s -> s.getRedirectionType() == redirectionType)
                            .findFirst();

                    if (newShorty.isPresent()){
                        return newShorty.get().getHashedUrl();
                    }
                //ako konekcija ne postoji, znaci da imamo zahtjev za novog usera za taj link
                }else {
                    //ako je to taj shorty koji smo pronasli daj mu konekciju sa tim shortijem,
                    // inace ide kreiranje novog shortyja
                    if(shorty.stream().anyMatch(s -> s.getRedirectionType() == redirectionType)){
                        Optional<Shorty> newShorty = shorty.stream()
                                .filter(s -> s.getRedirectionType() == redirectionType)
                                .findFirst();

                        if (newShorty.isPresent()){
                            UserShorty userShorty = new UserShorty();
                            userShorty.setUserEntity(loggedInUser);
                            userShorty.setShorty(newShorty.get());
                            userShorty.setId(new UserShortyId(loggedInUser.getAccountId(), newShorty.get().getId()));

                            userShortyRepository.save(userShorty);

                            return newShorty.get().getHashedUrl();
                        }
                    }
                }
            }

            String hashedUrl = passwordEncoder.encode(url)
                    .replaceAll("[/:?&=#]", "X")
                    .substring(40, 45);

            Shorty newShorty = new Shorty();
            newShorty.setOriginalUrl(url);
            newShorty.setHashedUrl(hashedUrl);
            newShorty.setRedirectionType(redirectionType);

            shortyRepository.save(newShorty);

            UserShorty userShorty = new UserShorty();
            userShorty.setUserEntity(loggedInUser);
            userShorty.setShorty(newShorty);
            userShorty.setId(new UserShortyId(loggedInUser.getAccountId(), newShorty.getId()));
            userShortyRepository.save(userShorty);

            return hashedUrl;
        }catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public ResolvedHashResponse resolveTheHashedUrl(String hashedUrl, String accountId) {
        Shorty shorty = shortyRepository.findByHashedUrl(hashedUrl)
                .orElseThrow();

        UserShortyId userShortyId = new UserShortyId(accountId, shorty.getId());

        UserShorty userShorty = userShortyRepository.findById(userShortyId)
                .orElseThrow();

        userShorty.setCounter(userShorty.getCounter() + 1);

        shortyRepository.save(shorty);

        return new ResolvedHashResponse(shorty.getOriginalUrl(), shorty.getRedirectionType());
    }

    public Map<String, Integer> getUsersShortyStatistics(String accountId){
        Map<String, Integer> redirects = new HashMap<>();

        List<UserShorty> shortiesData = userShortyRepository.findAllByUserEntityAccountIdWithShorty(accountId);

        shortiesData.forEach(data -> {
            redirects.put(data.getShorty().getOriginalUrl(), data.getCounter());
        });

        return redirects;
    }
}
