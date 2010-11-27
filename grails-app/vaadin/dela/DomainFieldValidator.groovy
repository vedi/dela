package dela

import com.vaadin.data.Validator
import java.text.MessageFormat

/**
 * @author vedi
 * date 25.11.10
 * time 21:04
 */
class DomainFieldValidator implements Validator {

    def propertyName
    def domain

    DomainFieldValidator() {
    }

    @Override
    boolean isValid(Object value) {
        domain.withTransaction {
            def oldValue = domain[propertyName]
            domain[propertyName] = value
            try {
                domain.validate()
            } finally {
                domain[propertyName] = oldValue
            }
        }
        !(domain.hasErrors() && domain.errors.getFieldError(propertyName))
    }

    void validate(Object value) throws Validator.InvalidValueException {
        if (!isValid(value)) {
            throw new Validator.InvalidValueException(MessageFormat.format(domain.errors.getFieldError(propertyName).defaultMessage, domain.errors.getFieldError(propertyName).arguments as Object[]));
        }
    }
}
