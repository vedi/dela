package dela.meta

/**
 * @author vedi
 * date 08.07.2010
 * time 20:35:28
 */
class MetaColumn {
    String field
    Class type = String.class
    def defaultValue
    boolean readOnly = false
    boolean sortable = false
}
