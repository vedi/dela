package dela.ui.common

import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.event.ShortcutAction.ModifierKey
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.ComponentContainer
import com.vaadin.ui.Form
import com.vaadin.data.Validator
import com.vaadin.ui.AbstractField
import com.vaadin.data.Validator.InvalidValueException

import com.vaadin.data.Item
import dela.Utils

/**
 * @author vedi
 * date 02.07.2010
 * time 23:15:53
 */
@Mixin(Utils)
class EntityForm extends Form implements Button.ClickListener {

    def dataContext
    def saveHandler
    def data

    def vaadinService

    boolean editable = false

    def defaultComponent = null

    Button okButton
    Button cancelButton

    def EntityForm() {
        this.writeThrough =  false
        this.invalidCommitted = false
        this.vaadinService = getBean(dela.VaadinService)
    }

    def void attach() {

        initButtons(footer)

        setItemDataSource(data as Item, getEditVisibleColumns())

        super.attach();

        if (defaultComponent) {
            defaultComponent.focus()
        }
    }

    protected void initButtons(ComponentContainer componentContainer) {
        if (editable) {
            okButton = new Button()
            okButton.caption = i18n('button.ok.label', 'ok')
            okButton.addListener(this as ClickListener)
            okButton.setClickShortcut(KeyCode.ENTER, ModifierKey.CTRL)
            componentContainer.addComponent(okButton)
        }

        cancelButton = new Button()
        cancelButton.caption = editable ? i18n('button.cancel.label', 'cancel') : i18n('button.close.label', 'close')
        cancelButton.addListener(this as ClickListener)
        cancelButton.setClickShortcut(KeyCode.ESCAPE)
        componentContainer.addComponent(cancelButton)
    }

    /**
     * TODO: Waiting <a href=http://dev.vaadin.com/ticket/3851>ticket</a>
     */
    @Override
    void validate() {
        validateField(this);
        for (final Iterator<Object> i = this.getItemPropertyIds().iterator(); i.hasNext();) {
            AbstractField field = this.getField(i.next());
            validateField(field)
        }
    }

    /**
     * TODO: Waiting <a href=http://dev.vaadin.com/ticket/3851>ticket</a>
     */
    @Override
    public boolean isValid() {
        boolean valid = true;
        for (final Iterator<Object> i = this.getItemPropertyIds().iterator(); i.hasNext();) {
            valid &= isFieldValid(this.getField(i.next()));
        }
        return valid && isFieldValid(this);
    }

    Boolean isFieldValid(AbstractField field) {
        if (field.isEmpty()) {
            if (field.isRequired()) {
                return false;
//            } else {
//                return true;
            }
        }

        if (field.validators == null) {
            return true;
        }

        final Object value = field.getValue();
        for (final Iterator<Validator> i = field.validators.iterator(); i.hasNext();) {
            if (!(i.next()).isValid(value)) {
                return false;
            }
        }

        return true;
    }

    void validateField(AbstractField field) {
        if (field.isEmpty()) {
            if (field.isRequired()) {
                throw new Validator.EmptyValueException(field.requiredError);
//            } else {
//                return;
            }
        }

        // If there is no validator, there can not be any errors
        if (field.validators == null) {
            return;
        }

        // Initialize temps
        Validator.InvalidValueException firstError = null;
        LinkedList<InvalidValueException> errors = null;
        final Object value = field.getValue();

        // Gets all the validation errors
        for (final Iterator<Validator> i = field.validators.iterator(); i.hasNext();) {
            try {
                (i.next()).validate(value);
            } catch (final Validator.InvalidValueException e) {
                if (firstError == null) {
                    firstError = e;
                } else {
                    if (errors == null) {
                        errors = new LinkedList<InvalidValueException>();
                        errors.add(firstError);
                    }
                    errors.add(e);
                }
            }
        }

        // If there were no error
        if (firstError == null) {
            return;
        }

        // If only one error occurred, throw it forwards
        if (errors == null) {
            throw firstError;
        }

        // Creates composite validator
        final Validator.InvalidValueException[] exceptions = new Validator.InvalidValueException[errors.size()];
        int index = 0;
        for (final Iterator<InvalidValueException> i = errors.iterator(); i.hasNext();) {
            exceptions[index++] = i.next();
        }

        throw new Validator.InvalidValueException(null, exceptions);
    }

    void buttonClick(ClickEvent clickEvent) {
        if (clickEvent.button == okButton) {
            commit()
            saveHandler(getItemDataSource())
        } else {
            discard()
        }

        window.close()
    }

    def getColumnLabel(columnName) {
        vaadinService.getColumnCaption(dataContext, columnName)
    }

    protected List<String> getEditVisibleColumns() {
        return vaadinService.getEditVisibleColumns(dataContext)
    }

    protected def addDomainValidator(AbstractField field, Item item, propertyId) {
        vaadinService.addDomainValidator(field, getDomain(item), propertyId)
    }

    protected def addServiceValidator(AbstractField field, Item item) {
        //TODO: Inject dataService
        vaadinService.addServiceValidator(field, null, dataContext, getDomain(item))
    }

}
