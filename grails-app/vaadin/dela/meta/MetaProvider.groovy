package dela.meta

import dela.State
import dela.Subject
import dela.Task
import dela.Version
import dela.Account

/**
 * @author vedi
 * date 28.06.2010
 * time 15:13:41
 */
class MetaProvider {

    public final MetaDomain taskMeta = new MetaDomain(
            domainClass: Task.class,
            columns: [
                    new MetaColumn(field: 'id', type:Long.class, readOnly: true),
                    new MetaColumn(field: 'name', readOnly: true),
                    new MetaColumn(field: 'description'),
                    new MetaColumn(field: 'power', type:Double.class, ),
                    new MetaColumn(field: 'subject', type:Subject.class),
                    new MetaColumn(field: 'state', type:State.class),
                    new MetaColumn(field: 'subjectVersion',type:Version.class),
                    new MetaColumn(field: 'dateCreated', type:Date.class),
            ],
    )

    public final MetaDomain stateMeta = new MetaDomain(domainClass: State.class,
            columns: [
                    new MetaColumn(field: 'name', readOnly: true, sortable: true),
            ],
    )

    public final MetaDomain subjectMeta = new MetaDomain(
            domainClass: Subject.class,
            columns: [
                    new MetaColumn(field: 'name', readOnly: true, sortable: true),
                    new MetaColumn(field: 'description'),
                    new MetaColumn(field: 'isPublic'),
            ],
    )

    public final MetaDomain accountMeta = new MetaDomain(
            domainClass: Account.class,
            columns: [
                    new MetaColumn(field: 'login', readOnly: true, sortable: true),
                    new MetaColumn(field: 'email', sortable: true),
                    new MetaColumn(field: 'password'),
                    new MetaColumn(field: 'role'),
                    new MetaColumn(field: 'state'),
            ],
    )
}
