package dela.ui.common

/**
 * @author vedi
 * date 12.12.10
 * time 23:00
 */
class SimpleEntityTable extends EntityTable {

    def gridFields
    def formFactory

    @Override
    protected createForm() {
        if (formFactory) {
            formFactory()
        } else {
            super.createForm()
        }
    }

}
