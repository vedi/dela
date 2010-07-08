package dela

import dela.container.DomainMeta

/**
 * @author vedi
 * date 08.07.2010
 * time 10:01:41
 */

class StateDvUtils {
    public static DomainMeta DEFAULT_DV = new DomainMeta(
            domainClass: State.class,
            caption: "States",
            columns: [
                    new DomainMeta.ColumnDefinition(field: 'name', readOnly: true, sortable: true),
            ],
            gridVisible: ['name'],
    )
}