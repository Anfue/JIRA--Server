/*********************************************/
/* Validacion disponible BUFFER              */
/* Autor: Jfernafu                           */
/*********************************************/
 
import com.atlassian.jira.component.ComponentAccessor
import com.opensymphony.workflow.InvalidInputException
import groovy.transform.Field
import Helper.CommonHelper
import Helper.GridHelper
import Helper.WSHelper 

 
//Constantes y managers
def cfm = ComponentAccessor.getCustomFieldManager()

@Field CommonHelper ch =  new CommonHelper()
@Field WSHelper wsh =  new WSHelper()
@Field GridHelper gh =  new GridHelper()

//def issue = ComponentAccessor.getIssueManager().getIssueObject("PPR-9505") 

 
def tgeCustomFieldId = 18120 //ID tabla PEP
def user = ch.getAdminUser() 
def gridData = gh.getAllGridRows(issue.id,tgeCustomFieldId,user)

gridData.each()//por cada Linea PEP del Grid
{

    if(it.pd.value == 'Si' && it.gic!=null && it.gic!=""){  //El PEP tiene GIC y PD
        def dotacion = it.y1+it.y2+it.y3+it.y4+it.y5
        def disponible =  wsh.disponibleBuffer()
		disponible =  disponible != null && disponible != '' ?  Double.parseDouble(disponible) : 0.0d
        
        if(disponible-dotacion <= 0)
        {
            sendEmail('PlanningFinanceSabis@mailinator.com', 'Asunto2', 'Body')
             throw new InvalidInputException("El PPR ha quedado pendiente de dotación a la espera de incrementar el buffer de gasto por parte de Finance")
        }
        
     
    }
}

String sendEmail(String emailAddr, String subject, String body) {
    def logger = Logger.getLogger(getClass())
    logger.setLevel(Level.DEBUG)

    // Stop emails being sent if the outgoing mail server gets disabled (useful if you start a script sending emails and need to stop it)
    def mailSettings = ComponentAccessor.getComponent(MailSettings)
    if (mailSettings?.send()?.disabled) {
        return 'Your outgoing mail server has been disabled'
    }

    def mailServer = ComponentAccessor.mailServerManager.defaultSMTPMailServer
    if (!mailServer) {
        logger.debug('Your mail server Object is Null, make sure to set the SMTP Mail Server Settings Correctly on your Server')
        return 'Failed to Send Mail. No SMTP Mail Server Defined'
    }

    def email = new Email(emailAddr)
    email.setMimeType('text/html')
    email.setSubject(subject)
    email.setBody(body)
    try {
        // This is needed to avoid the exception about IMAPProvider
        ContextClassLoaderSwitchingUtil.runInContext(SMTPMailServer.classLoader) {
            mailServer.send(email)
        }
        logger.debug('Mail sent')
        'Success'
    } catch (MailException e) {
        logger.debug("Send mail failed with error: ${e.message}")
        'Failed to Send Mail, Check Logs for error'
    }
}


             