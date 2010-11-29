package dela

import com.vaadin.data.Validator
import java.text.MessageFormat

/**
 * @author vedi
 * date 28.11.10
 * time 15:49
 */
class ServiceValidator implements Validator {

    def dataService
    def dataContext
    def domain

    ServiceValidator() {
    }

    @Override
    boolean isValid(Object value) {
        return dataService.canSave(dataContext, domain, value)
    }

    void validate(Object value) throws Validator.InvalidValueException {
        if (!isValid(value)) {
            throw new Validator.InvalidValueException('can\'t save');
        }
    }
}
