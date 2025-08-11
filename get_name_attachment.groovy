import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue

//def issue = event.issue as MutableIssue
/*****ACCESO A LA CONSOLA******/
def issue = Issues.getByKey('HDEV-420351')
def boleano = false

   Issues.getByKey('HDEV-420351').attachments.each { attachment ->
                // do something with "attachment"
                log.warn(attachment.filename)
                if(attachment.filename.contains("DF_Diseño Funcional") && (attachment.filename.contains("DT_Diseño Tecnico") || 
                attachment.filename.contains("DT_Diseño Técnico")) && ){
                    boleano = true
                }
                log.warn('attachment.filename->'+attachment.filename)
            }
return boleano
