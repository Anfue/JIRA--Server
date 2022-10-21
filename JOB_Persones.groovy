/***********************************************************/
/* Script lectura fichero Maestros HDEV                    */
/* Tarifas                                                 */
/* Autor: Jfernafu                                         */
/***********************************************************/

import groovy.transform.Field
import Helper.BDHelper
import java.io.File
 
Calendar now = Calendar.getInstance();
def year = now.get(Calendar.YEAR)
def month = (now.get(Calendar.MONTH) + 1)  < 10 ? "0"+(now.get(Calendar.MONTH) + 1) : (now.get(Calendar.MONTH) + 1)
def day =  now.get(Calendar.DAY_OF_MONTH) < 10 ? "0"+ now.get(Calendar.DAY_OF_MONTH) : now.get(Calendar.DAY_OF_MONTH)

//Constantes y managers
String PATH = "/app/FILEBS"
//String PATH = "/app/atlassian/jira"
//String FILE_NAME =   "MFS_MAESTRO_${year}${month}${day}_OUT.txt"
String FILE_NAME ="PERSONITAS.txt"
//String FILE_NAME ="mfs_tarifas.txt"

@Field BDHelper bdh =  new BDHelper()

//String LIMP_SQL = "delete  from [JIRAAUX].[dbo].[USUARIO]" //REVISAR TABLA

//def SQL = "select * from [JIRAAUX].[dbo].[USUARIO]"

//return bdh.selectHalley(SQL)
					
String INSERT_SQL = "INSERT INTO [dbo].[USUARIO]  ([NIF],[USUARIO] ,[EMPRESA],[UNIDAD] ,[COD_FUN])"

def user

def UserConLetra(nif){
	user = nif.toString()
	//El string de letras es fijo y deberían de ir en este orden
	String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
	//La letra correspondiente será el resto de la división del número del DNI entre las 23 posibilidades que hay
	char letra = letras.charAt(nif % letras.length());
	String dniEntero =""+ letra + user[2,3,4,5,6,7]
	return(dniEntero);
    }


//MAIN
File file = new File( PATH+'/'+FILE_NAME)
//def lines = file.getText('ISO-8859-1').split('\n') 
def lines = file.getText('UTF-8').split('\n') 

//limpiarTarifa(LIMP_SQL)


lines.each
{
 //log.warn(it)
       
    def nif =  it.substring(10,19)
	def usuario =  it.substring(134,141)
    def empresa =  it.substring(79,83)
	def unidad =  it.substring(83,87)
    def cod_fun =  it.substring(91,95)
    empresa = empresa.toInteger()
    empresa = empresa.toString()
	//nif = UserConLetra(nif)	 
    log.warn("nif: ${nif} usuario: ${usuario} empresa: ${empresa} unidad: ${unidad}  cod_fun: ${cod_fun} ")
		 
	EXEC_SQL = INSERT_SQL+"values('${nif.toString().trim()}','${usuario.toString().trim()}','${empresa.trim()}','${unidad.toString().trim()}','${cod_fun.toString().trim()}')"
          
	insertarDatos(EXEC_SQL)
}

//METODOS 

def limpiarTarifa(String LIMP_SQL)
{
	bdh.executeHalley(LIMP_SQL)
}

//Ejecucion INSERT en BBDD
def insertarDatos(String SQL)
{
    bdh.executeHalley(SQL)
}