package asee.shortycore.models;

import asee.shortycore.ShortyCoreApplication;
import asee.shortycore.models.authentication.UserModel;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest(classes = ShortyCoreApplication.class)
public class UserModelTests {
    @Autowired
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testWhenAccountIdLengthIsGreaterThan50ThrowValidation() {
        // Arrange
        UserModel userModel = new UserModel();
        userModel.setAccountId("123456789012345678901234567890123456789012345678901");

        // Act
        Set<ConstraintViolation<UserModel>> violations = validator.validate(userModel);

        // Assert
        Assertions.assertThat(violations)
                .hasSize(1)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("Account ID must be between 1 and 50 characters long!");
    }

    @Test
    void testWhenAccountIdLengthIsLessThan50ViolationsAreEmpty() {
        // Arrange
        UserModel userModel = new UserModel();
        userModel.setAccountId("123456901");

        // Act
        Set<ConstraintViolation<UserModel>> violations = validator.validate(userModel);

        // Assert
        Assertions.assertThat(violations)
                .hasSize(0);
    }
}
