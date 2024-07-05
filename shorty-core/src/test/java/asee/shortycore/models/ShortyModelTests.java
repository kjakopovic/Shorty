package asee.shortycore.models;

import asee.shortycore.ShortyCoreApplication;
import asee.shortycore.models.shorty.ShortyModel;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest(classes = ShortyCoreApplication.class)
public class ShortyModelTests {
    private static final String InvalidUrl = "Url1psrjgiorsjifjiojbswrhgursigjfdigjsriohjguiosrjioghjbfilsjioashjifrduihbsruigheaiofjiosrejgiudrhgruiheaifjoisjgirdhgursghuiojseofijiongbuosrhgiuosjfiojsdiogbdruhgusrefhhjsdnguisrhfiosjdkgnvgsriojgiosjfkdghsdr";

    @Autowired
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @CsvSource({
            InvalidUrl + ", Hash1",
            "Url2, Hash2sadsadsadasd"
    })
    void testWhenLengthOfHashedUrlAndOriginalUrlIsInvalidThrowValidation(String originalUrl, String hashedUrl) {
        // Arrange
        var shortyModel = new ShortyModel();
        shortyModel.setId(1);
        shortyModel.setOriginalUrl(originalUrl);
        shortyModel.setHashedUrl(hashedUrl);
        shortyModel.setRedirectionType(301);

        // Validate the UserModel
        Set<ConstraintViolation<ShortyModel>> violations = validator.validate(shortyModel);

        // Assert that there is exactly one violation for the accountId field
        Assertions.assertThat(violations)
                .hasSize(1);
    }

    @Test
    void testWhenLengthOfHashedUrlAndOriginalUrlIsValidReturnNoViolations() {
        // Arrange
        var shortyModel = new ShortyModel();
        shortyModel.setId(1);
        shortyModel.setOriginalUrl("Url");
        shortyModel.setHashedUrl("Hash");
        shortyModel.setRedirectionType(301);

        // Validate the UserModel
        Set<ConstraintViolation<ShortyModel>> violations = validator.validate(shortyModel);

        // Assert that there is exactly one violation for the accountId field
        Assertions.assertThat(violations)
                .hasSize(0);
    }
}
