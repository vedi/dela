package dela

class MetaService {

    def messageService

    static transactional = true

    def getMetaListCaption(metaDomain) {
        String entityName = metaDomain.domainClass.simpleName
        messageService.getEntityListCaptionMsg(entityName.toLowerCase())
    }

    def getMetaCaption(metaDomain) {
        String entityName = metaDomain.domainClass.simpleName
        messageService.getEntityCaptionMsg(entityName.toLowerCase())
    }

    def getColumnCaption(metaDomain, column) {
        String entityName = metaDomain.domainClass.simpleName
        messageService.getFieldLabelMsg(entityName.toLowerCase(), column)
    }
}
