package dela.ui.common

/**
 * @author vedi
 * date 12.12.10
 * time 23:00
 */
class SimpleEntityTable extends EntityTable {

    def gridFields
    def formFactory
    def formFactoryParams

    @Override
    protected createForm() {
        if (formFactory) {
            formFactory(formFactoryParams)
        } else {
            super.createForm()
        }
    }

}
