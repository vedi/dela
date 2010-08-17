package dela.ui.common

import com.vaadin.data.Container
import com.vaadin.data.Item
import com.vaadin.data.util.BeanItem
import com.vaadin.event.ItemClickEvent
import com.vaadin.event.ShortcutAction
import com.vaadin.event.ShortcutListener
import com.vaadin.terminal.FileResource
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.Form
import com.vaadin.ui.FormFieldFactory
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Table
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Window
import dela.YesNoDialog
import dela.common.ItemUtils
import dela.meta.MetaDomain

/**
 * @author vedi
 * date 28.06.2010
 * time 18:11:29
 */
public abstract class AbstractEntityTable extends VerticalLayout implements ClickListener {

    def MetaDomain metaDomain

    Table table

    HorizontalLayout toolBarLayout

    Button addButton
    Button editButton
    Button deleteButton
    Button refreshButton

    protected Container container

    def formFieldFactory

    def saveHandler = {item ->
        metaDomain.domainClass.withTransaction {
            Long id = item.getItemProperty("id")?.value as Long
            boolean isNew = id == null
            def domain
            if (isNew) {
                domain = metaDomain.domainClass.newInstance()
            } else {
                domain = metaDomain.domainClass.get(id)
                assert domain
            }

            ItemUtils.itemToDomain(item, domain)

            assert domain.save(), domain.errors

            this.refresh()
            if (isNew) {
                item.getItemProperty("id").value = domain.id
                this.select(domain.id)
            }
        }
    }

    def AbstractEntityTable() {
        table = new Table()
    }

    def setDropHandler(dropHandler) {
        table.dropHandler = dropHandler
    }

    @Override
    public void attach() {

        assert metaDomain

        table.addShortcutListener(new ShortcutListener("down", ShortcutAction.KeyCode.ARROW_DOWN, new int[0]) {
            void handleAction(Object o, Object o1) {
                AbstractEntityTable.this.table.select(AbstractEntityTable.this.table.nextItemId(AbstractEntityTable.this.table.getValue()))
            }
        });
        table.addShortcutListener(new ShortcutListener("up", ShortcutAction.KeyCode.ARROW_UP, new int[0]) {
            void handleAction(Object o, Object o1) {
                AbstractEntityTable.this.table.select(AbstractEntityTable.this.table.prevItemId(AbstractEntityTable.this.table.getValue()))
            }
        });

        toolBarLayout = new HorizontalLayout();
        initToolBar(toolBarLayout)
        this.addComponent toolBarLayout

        initTable()

        super.attach()

    }

    protected void initToolBar(toolBar) {

        addButton = new Button();
        addButton.setDescription(i18n('button.create.label', 'create'))
        addButton.setClickShortcut(ShortcutAction.KeyCode.INSERT)
        addButton.setIcon(new FileResource(new File('web-app/images/skin/database_add.png'), this.window.application))
        addButton.addListener(this as ClickListener)
        toolBar.addComponent addButton

        editButton = new Button();
        editButton.setDescription(i18n('button.edit.label', 'edit'))
        editButton.setClickShortcut(ShortcutAction.KeyCode.ENTER)
        editButton.setIcon(new FileResource(new File('web-app/images/skin/database_edit.png'), this.window.application))
        editButton.addListener(this as ClickListener)
        toolBar.addComponent editButton

        deleteButton = new Button();
        deleteButton.setDescription(i18n('button.delete.label', 'delete'))
        deleteButton.setClickShortcut(ShortcutAction.KeyCode.DELETE)
        deleteButton.setIcon(new FileResource(new File('web-app/images/skin/database_delete.png'), this.window.application))
        deleteButton.addListener(this as ClickListener)
        toolBar.addComponent deleteButton

        refreshButton = new Button();
        refreshButton.setDescription(i18n('button.refresh.label', 'refresh'))
        refreshButton.setIcon(new FileResource(new File('web-app/images/skin/database_refresh.png'), this.window.application))
        refreshButton.addListener(this as ClickListener)
        toolBar.addComponent refreshButton
    }

    // TODO: What's this?
    def setContainerDataSource(containerDataSource) {
        table.containerDataSource = containerDataSource
    }

    protected void initTable() {

        this.table.immediate = true
        this.table.selectable = true
        this.table.nullSelectionAllowed = false

        String entityName = metaDomain.domainClass.simpleName

        this.caption = i18n("entity.${entityName.toLowerCase()}.many.caption", "${entityName} list")

        this.table.addListener(new ItemClickEvent.ItemClickListener() {
            public void itemClick(ItemClickEvent event) {
                if (event.doubleClick) {
                    showForm(event.item, canEdit(event.item))
                }
            }
        })

        container = createContainer(this.metaDomain)

        this.table.setContainerDataSource(this.container)

        this.table.setVisibleColumns(getGridVisibleColumns() as Object[]);
        this.table.setColumnHeaders(getColumnHeaders())

        initGrid();

        this.table.setSizeFull()

        this.addComponent(this.table)
        this.setExpandRatio(this.table, 1.0f)
    }

    abstract protected Container createContainer(metaDomain)

    abstract protected void refreshContainer()

    def initGrid() {
    }

    public void refresh() {
        refreshContainer()
    }

    void buttonClick(ClickEvent clickEvent) {
        if (clickEvent.button == addButton) {
            if (canInsert()) {
                showForm(new BeanItem(createDomain()))
            }
        } else if (clickEvent.button == editButton) {
            if (table.value != null) {
                def item = container.getItem(table.value)
                if (item) {
                    showForm(item, canEdit(item))
                }
            }
        } else if (clickEvent.button == deleteButton) {
            if (table.value != null) {
                def item = container.getItem(table.value)
                if (item && canDelete(item)) {
                    remove(item)
                }
            }
        } else if (clickEvent.button == refreshButton) {
            refresh()
        }
    }

    void remove(Item item) {
        this.window.application.mainWindow.addWindow(new YesNoDialog(
                i18n('button.delete.confirm.caption', 'confirm delete'),
                i18n('button.delete.confirm.message', 'are you sure?'),
                i18n('button.yes.label', 'yes'),
                i18n('button.no.label', 'no'),
                new YesNoDialog.Callback() {
                    public void onDialogResult(boolean yes) {
                        if (yes) {
                            Long id = item.getItemProperty("id")?.value as Long
                            assert id
                            AbstractEntityTable.this.doRemove(id)
                        }
                    }

                }))
    }

    protected void doRemove(long id) {
        metaDomain.domainClass.withTransaction {
            def domain = metaDomain.domainClass.get(id)
            assert domain

            domain.delete()
            refresh()
        }
    }
    void showForm(selectedItem, editable = true) {
        String entityName = metaDomain.domainClass.simpleName
        Window window = new Window(i18n("entity.${entityName.toLowerCase()}.caption", "${entityName}"))

        Form form = createForm()
        form.editable = editable

        if (getFormFieldFactory()) {
            form.formFieldFactory = getFormFieldFactory() as FormFieldFactory
        }

        metaDomain.domainClass.withTransaction {
            form.itemDataSource =  selectedItem
        }
        form.visibleItemProperties = getEditVisibleColumns()
        form.saveHandler = saveHandler

        window.addComponent(form)

        window.layout.setSizeUndefined()
        window.center()

        this.window.application.mainWindow.addWindow window
    }

    protected List<String> getGridVisibleColumns() {
        return metaDomain.columns.collect {it.field}
    }

    protected List<String> getEditVisibleColumns() {
        metaDomain.columns.collect {it.field}
    }

    protected Form createForm() {
        return new EntityForm()
    }

    protected createDomain() {
        return metaDomain.domainClass.newInstance()
    }

    protected String[] getColumnHeaders() {
        return getGridVisibleColumns().collect {getColumnLabel(it)} as String[]
    }

    protected select(Long id) {
        def itemIds = this.container.getItemIds()
        for (itemId in itemIds) {
            if (this.container.getItem(itemId).getItemProperty('id').value.equals(id)) {
                this.table.setValue(itemId)
                break
            }
        }
    }

    def getColumnLabel(columnName) {
        i18n("entity.${metaDomain.domainClass.simpleName.toLowerCase()}.field.${columnName}.label", columnName)
    }

    def canDelete(item) {
        return true
    }

    def canInsert() {
        return true
    }

    def canEdit(item) {
        return true
    }


}
