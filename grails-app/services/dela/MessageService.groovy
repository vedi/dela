package dela

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.NoSuchMessageException

class MessageService {

    def MessageSource messageSource

    static transactional = true

    public String getEntityListCaptionMsg(String entityName) {
        getMessage("entity.${entityName}.many.caption")
    }

    public String getEntityCaptionMsg(String entityName) {
        getMessage("entity.${entityName}.caption")
    }

    public String getFieldLabelMsg(String entityName, String columnName) {
        getMessage("entity.${entityName}.field.${columnName}.label")
    }

    public String getAuthFailedMsg() {
        getMessage('auth.failed.message')
    }

    public String getForgetPasswordWindowCaptionMsg() {
        getMessage('window.forgetPassword.caption')
    }

    public String getForgetPasswordSuccessMsg() {
        getMessage('forgetPassword.success.message')
    }

    public String getForgetPasswordFailedMsg() {
        getMessage('forgetPassword.failed.message')
    }

    public String getRegistrationSuccessMsg() {
        getMessage('registration.success.message')
    }

    public String getRegistrationFailedMsg() {
        getMessage('registration.failed.message')
    }

    public String getLoginButtonLabel() {
        getMessage('button.login.label')
    }

    public String getProfileButtonLabel() {
        getMessage('button.profile.label')
    }

    public String getLogoutButtonLabel() {
        getMessage('button.logout.label')
    }

    public String getRegisterButtonLabel() {
        getMessage('button.register.label')
    }

    public String getLoggedInfoMsg(Object user) {
        getMessage('loggedUser.info.message', [user])
    }

    public String getConfirmRegistrationMailTitle() {
        getMessage('mail.confirmRegistration.title')
    }

    public String getConfirmRegistrationMailBody(params) {
        getMessage('mail.confirmRegistration.body', params)
    }

    public String getResetPasswordMailTitle() {
        getMessage('mail.resetPassword.title')
    }

    public String getResetPasswordMailBody(params) {
        getMessage('mail.resetPassword.body', params)
    }

    public String getSetSubjectActiveConfirmCaption() {
        getMessage('setSubjectActive.confirm.caption')
    }

    public String getSetSubjectActiveConfirmMsg() {
        getMessage('setSubjectActive.confirm.message')
    }

    public String getYesButtonLabel() {
        getMessage('button.yes.label')
    }

    public String getNoButtonLabel() {
        getMessage('button.no.label')
    }

    public String getNormalizeButtonLabel() {
        getMessage('button.normalize.label')
    }

    public String getCompleteButtonLabel() {
        getMessage('button.complete.label')
    }

    public String getCompleteConfirmCaption() {
        getMessage('complete.confirm.caption')
    }

    public String getCompleteConfirmMsg() {
        getMessage('complete.confirm.message')
    }

    public String getNormalizeIsCompletedMsg() {
        getMessage('normalize.complete.message')
    }

    public String getLoginWindowCaptionMsg() {
        getMessage('window.login.caption')
    }

    public String getForgetPasswordButtonLabelMsg() {
        getMessage('button.forgetPassword.label')
    }

    public String getMessage(String key, params) {
        try {
            return messageSource.getMessage(key, params as Object[], LocaleContextHolder.getLocale())
        } catch (NoSuchMessageException e) {
            log.warn("", e)
            return "$key"
        }
    }

    public String getMessage(String key) {
        getMessage(key, [])
    }

}
