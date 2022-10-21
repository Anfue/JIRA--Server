//Resumen BC - tabla derecha ficha resumen (slide 3) PRODUCCION
import java.text.NumberFormat;
import Helper.GridHelper
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import groovy.transform.Field
import com.atlassian.jira.ComponentManager;
import Helper.CommonHelper
import Helper.BDHelper
import Helper.GridHelper
import Helper.NfeedHelper
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue

def IRR = 11770
def cost = 12215
def benefit = 14603//12211

def CAPEX = 11733
def OPEX = 11732
def CRMC = 11736
def CRMO = 11734
def RLA = 11738
def COSTAV = 11739
def REVI = 11740
def CASH = 11746
def KPI = 12213//11741
def HITO = 12214//11742

def BE =     11771
def OPEXIM = 11756
def AMORT =  11800
def TOTAL =  11801
def NAMORT = 12224
def RAMORT = 12225
def EFF =    11747

//issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("BC-3971")

final Calendar cal = Calendar.getInstance();
//cal.setTime(issue.created)
//inicio vinculacion con year1
def cfyear1id = 12706
def cfyear1 =  ComponentAccessor.getCustomFieldManager().getCustomFieldObject(cfyear1id)
int actual_year = issue.getCustomFieldValue(cfyear1)
cal.set(Calendar.YEAR,actual_year)
/**************************************nuevo *******************************/
@Field GridHelper gh = new GridHelper()
@Field CommonHelper ch = new CommonHelper()
@Field NfeedHelper nfh = new NfeedHelper()
@Field BDHelper bdh = new BDHelper()
def user = ch.getAdminUser()     
def gridRecurrentes = 16231L // Costes recurrentes
def gridIDNIT = 16233L // Costes NO IT
def gridDataRec = gh.getAllGridRows(issue.id, gridRecurrentes, user)
def gridDataNIT = gh.getAllGridRows(issue.id, gridIDNIT, user)
def ano1=0;def ano2=0;def ano3=0;def ano4=0;def ano5=0;totrec=0
def ano1NIT=0;def ano2NIT=0;def ano3NIT=0;def ano4NIT=0;def ano5NIT=0;totNIT=0
/**************************************nuevo *******************************/
def cf_irr = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(IRR)
def cf_cost = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(cost)
def cf_benefit = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(benefit)
log.error('Beneficion valor -->'+issue.getCustomFieldValue(cf_benefit))
def val_capex = 0;def val_opex =  0;val_capex1 =  0;val_capex2 =  0;val_capex3 =  0;val_capex4 =  0;val_capex5 =  0;val_opex1 =  0;val_opex2 =  0;val_opex3 =  0;val_opex4 =  0;val_opex5 =  0;val_opex1Num = 0
val_opex2Num = 0;val_opex3Num = 0;val_opex4Num = 0;val_opex5Num = 0;val_prior_capex = 0;val_prior_opex = 0;int newAmort1 = 0;int newAmort2 = 0;int newAmort3 = 0;int newAmort4 = 0;int newAmort5 = 0
double reducAmort1 = 0;double reducAmort2 = 0;double reducAmort3 = 0;double reducAmort4 = 0;double reducAmort5 = 0;double opexIPD1 = 0;double opexIPD2 = 0;double opexIPD3 = 0;double opexIPD4 = 0;double opexIPD5 = 0
//MET-897
capexITprior = 0;capexIT1 = 0;capexIT2 = 0;capexIT3 = 0;capexIT4 = 0;capexIT5 = 0;capexITT = 0;capexNONITprior = 0;capexNONIT1 = 0;capexNONIT2 = 0;capexNONIT3 = 0;capexNONIT4 = 0;capexNONIT5 = 0;capexNONITT = 0
opexITprior = 0;opexIT1 = 0;opexIT2 = 0;opexIT3 = 0;opexIT4 = 0;opexIT5 = 0;opexITT = 0;opexNONITprior = 0;opexNONIT1 = 0;opexNONIT2 = 0;opexNONIT3 = 0;opexNONIT4 = 0;opexNONIT5 = 0;opexNONITT = 0;opexRECprior = 0;opexREC1 = 0;opexREC2 = 0;opexREC3 = 0;opexREC4 = 0;opexREC5 = 0;opexRECT = 0
//MET-897
cf_categoria = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(11500)
def val_cost = (issue.getCustomFieldValue(cf_cost) != null ? issue.getCustomFieldValue(cf_cost) : 0 )
if(val_cost != 0)
{
    val_cost = val_cost.replace("[","").replace("]","").split(",")
  
        val_capex =  (val_cost != 0 ? (val_cost.size()-1 <13 ? 0 :  Double.parseDouble(val_cost[13]) != 0 ?   (Double.parseDouble(val_cost[49]) + Double.parseDouble(val_cost[13])).round(2) : val_cost[49] ) : 0)
        val_capex1 =  (  val_cost.size()-1 < 15  ? 0:  val_cost[15] != 0 ?     (Double.parseDouble(val_cost[44]) + Double.parseDouble(val_cost[15])).intValue() : val_cost[44]   )
        val_capex2 =   (  val_cost.size()-1 < 16  ? 0:  val_cost[16] != 0 ?      (Double.parseDouble(val_cost[45]) + Double.parseDouble(val_cost[16])).intValue() : val_cost[45]   )
        val_capex3 = (  val_cost.size()-1 < 17  ? 0:  val_cost[17] != 0 ?       (Double.parseDouble(val_cost[46]) + Double.parseDouble(val_cost[17])).intValue() : val_cost[46]   )
        val_capex4 = (  val_cost.size()-1 < 18  ? 0:  val_cost[18] != 0 ?      (Double.parseDouble(val_cost[47]) + Double.parseDouble(val_cost[18])).intValue() : val_cost[47]   )
        val_capex5 = (  val_cost.size()-1 < 19  ? 0:  val_cost[19] != 0 ?      (Double.parseDouble(val_cost[48]) + Double.parseDouble(val_cost[19])).intValue() : val_cost[48]   )
    
    val_opex =  (val_cost != 0 ? (val_cost.size()-1 <14 ? 0 :  Double.parseDouble(val_cost[14]) != 0 ?   (Double.parseDouble(val_cost[11]) + Double.parseDouble(val_cost[14])).round(2) : val_cost[11] ) : 0)
    val_opex1 =  (  val_cost.size()-1 < 20  ? 0:  val_cost[20] != 0 ?       (Double.parseDouble(val_cost[6]) + Double.parseDouble(val_cost[20])).intValue() : val_cost[6]   )
    val_opex2 =  (  val_cost.size()-1 < 21  ? 0:  val_cost[21] != 0 ?       (Double.parseDouble(val_cost[7]) + Double.parseDouble(val_cost[21])).intValue() : val_cost[7]   )
    val_opex3 =  (  val_cost.size()-1 < 22  ? 0:  val_cost[22] != 0 ?       (Double.parseDouble(val_cost[8]) + Double.parseDouble(val_cost[22])).intValue() : val_cost[8]   )
    val_opex4 =  (  val_cost.size()-1 < 23  ? 0:  val_cost[23] != 0 ?       (Double.parseDouble(val_cost[9]) + Double.parseDouble(val_cost[23])).intValue() : val_cost[9]   )
    val_opex5 =  (  val_cost.size()-1 < 24  ? 0:  val_cost[24] != 0 ?       (Double.parseDouble(val_cost[10]) + Double.parseDouble(val_cost[24])).intValue() : val_cost[10]   )
    val_opex1Num = (val_cost.size()-1 < 20  ? 0: esCR()?  (Double.parseDouble(val_cost[6]) + Double.parseDouble(val_cost[20])).intValue(): val_cost[6])
    val_opex2Num = (val_cost.size()-1 < 21  ? 0: esCR()?  (Double.parseDouble(val_cost[7]) + Double.parseDouble(val_cost[21])).intValue() : val_cost[7])
    val_opex3Num = (val_cost.size()-1 < 22  ? 0: esCR()?  (Double.parseDouble(val_cost[8]) + Double.parseDouble(val_cost[22])).intValue(): val_cost[8])
    val_opex4Num = (val_cost.size()-1 < 23  ? 0: esCR()?  (Double.parseDouble(val_cost[9]) + Double.parseDouble(val_cost[23])).intValue():  val_cost[9])
    val_opex5Num = (val_cost.size()-1 < 24  ? 0: esCR()?  (Double.parseDouble(val_cost[10]) + Double.parseDouble(val_cost[24])).intValue(): val_cost[10])
    val_prior_capex =  val_cost.size()-1 < 25 ?  0 :  Double.parseDouble(val_cost[25]).intValue()
    val_prior_opex =   val_cost.size()-1 < 26 ?  0 :  Double.parseDouble(val_cost[26]).intValue()
    newAmort1 =   val_cost.size()-1 < 32 ? 0 : Math.round(Double.parseDouble(val_cost[32]))
    newAmort2 =   val_cost.size()-1 < 33 ? 0 : Math.round(Double.parseDouble(val_cost[33]))
    newAmort3 =   val_cost.size()-1 < 34 ? 0 : Math.round(Double.parseDouble(val_cost[34]))
    newAmort4 =   val_cost.size()-1 < 35 ? 0 : Math.round(Double.parseDouble(val_cost[35]))
    newAmort5 =   val_cost.size()-1 < 36 ? 0 : Math.round(Double.parseDouble(val_cost[36]))
    reducAmort1 =  val_cost.size()-1 < 37 ? 0 : Math.round(Double.parseDouble(val_cost[37]))
    reducAmort2 = val_cost.size()-1 < 38 ? 0 : Math.round(Double.parseDouble(val_cost[38]))
    reducAmort3 = val_cost.size()-1 < 39 ? 0 : Math.round(Double.parseDouble(val_cost[39]))
    reducAmort4 = val_cost.size()-1 < 40 ? 0 : Math.round(Double.parseDouble(val_cost[40]))
    reducAmort5 = val_cost.size()-1 < 41 ? 0 : Math.round(Double.parseDouble(val_cost[41]))
		//if(esProyecto() || esProyectoComoServicio())
		//{
			opexIPD1  =  val_cost.size() > 137 ?  Double.parseDouble(val_cost[137]) .intValue() : 0 
			opexIPD2 =   val_cost.size() > 137 ? Double.parseDouble(val_cost[138]).intValue() : 0 
			opexIPD3 =  val_cost.size() > 137 ?  Double.parseDouble(val_cost[139]).intValue() : 0 
			opexIPD4 =  val_cost.size() > 137 ?  Double.parseDouble(val_cost[140]).intValue() : 0 
			opexIPD5 =  val_cost.size() > 137 ?  Double.parseDouble(val_cost[141]).intValue()	 : 0 	
	//}else
	//	{
	//		opexIPD1  =  val_cost.size()-1 < 27 ? 0 : Double.parseDouble(val_cost[27]) .intValue()
	//		opexIPD2 =  val_cost.size()-1 < 28 ? 0 : Double.parseDouble(val_cost[28]).intValue()
	//		opexIPD3 =  val_cost.size()-1 < 29 ? 0 : Double.parseDouble(val_cost[29]).intValue()
	//		opexIPD4 =  val_cost.size()-1 < 30 ? 0 : Double.parseDouble(val_cost[30]).intValue()
	//		opexIPD5 =  val_cost.size()-1 < 31 ? 0 : Double.parseDouble(val_cost[31]).intValue()
//		}   
    //MET-897
    if(val_cost.size()-1 > 70)
    {
        capexITprior = (Double.parseDouble(val_cost[25]) - Double.parseDouble(val_cost[80])) 
	  //  if(esProyecto())
	//	{
			capexIT1 = val_cost.size() > 113 ? Double.parseDouble(val_cost[113]) : 0
			log.error('CAPEX IT-------------------------------->'+val_cost)
			capexIT2 = val_cost.size() > 113 ?  Double.parseDouble(val_cost[114]) : 0
			capexIT3 = val_cost.size() > 113 ?  Double.parseDouble(val_cost[115]): 0
			capexIT4 = val_cost.size() > 113 ?  Double.parseDouble(val_cost[116]) : 0
			capexIT5 = val_cost.size() > 113 ?  Double.parseDouble(val_cost[117])	 : 0		
		//}else if(esProyectoComoServicio())
	//	{
	//		capexIT1 =  Double.parseDouble(val_cost[95])
	//		capexIT2 =  Double.parseDouble(val_cost[96])
	//		capexIT3 =  Double.parseDouble(val_cost[97])
	//		capexIT4 =  Double.parseDouble(val_cost[98])
	//		capexIT5 =  Double.parseDouble(val_cost[99])		
	//	}else
	//	{
	//		capexIT1 = Double.parseDouble(val_cost[0]) + ( Double.parseDouble(val_cost[15]) - Double.parseDouble(val_cost[62]) )
	//		capexIT2 = Double.parseDouble(val_cost[1]) + ( Double.parseDouble(val_cost[16]) - Double.parseDouble(val_cost[63]) )
	//		capexIT3 = Double.parseDouble(val_cost[2]) + ( Double.parseDouble(val_cost[17]) - Double.parseDouble(val_cost[64]) )
	//		capexIT4 = Double.parseDouble(val_cost[3]) + ( Double.parseDouble(val_cost[18]) - Double.parseDouble(val_cost[65]) )
	//		capexIT5 = Double.parseDouble(val_cost[4]) + ( Double.parseDouble(val_cost[19]) - Double.parseDouble(val_cost[66]) )		
	//	}    
		
        //*NUEVO PRIOR MFS**//
        capexITT = capexIT1 + capexIT2 + capexIT3 + capexIT4 + capexIT5	+	capexITprior //PRIOR MFS3
        
        //*NUEVO PRIOR MFS**//
        
        //capexITT = capexIT1 + capexIT2 + capexIT3 + capexIT4 + capexIT5		
        capexNONITprior = Double.parseDouble(val_cost[80])
      // if(esProyecto()){
			capexNONIT1 = val_cost.size() > 119 ? Double.parseDouble(val_cost[119]) : 0 ;capexNONIT2 =  val_cost.size() > 119 ? Double.parseDouble(val_cost[120]) : 0;		capexNONIT3 = val_cost.size() > 119 ? Double.parseDouble(val_cost[121]):0 ;capexNONIT4 =val_cost.size() > 119 ?  Double.parseDouble(val_cost[122]) :0 
			capexNONIT5 = val_cost.size() > 119 ? Double.parseDouble(val_cost[123]) :0 ;	//capexNONITT =Double.parseDouble(val_cost[124])
	//	}else if(esProyectoComoServicio()){
			//capexNONIT1 =0;capexNONIT2 =0;capexNONIT3 =0;capexNONIT4 =0;capexNONIT5 =0; //capexNONITT =0
	//	}
		//else{
		//capexNONIT1 =  ( Double.parseDouble(val_cost[44]) - Double.parseDouble(val_cost[0])  ) +  Double.parseDouble(val_cost[62])
        //capexNONIT2 =  ( Double.parseDouble(val_cost[45]) - Double.parseDouble(val_cost[1])  ) +  Double.parseDouble(val_cost[63])
        //capexNONIT3 =  ( Double.parseDouble(val_cost[46]) - Double.parseDouble(val_cost[2])  ) +  Double.parseDouble(val_cost[64])
        //capexNONIT4 =  ( Double.parseDouble(val_cost[47]) - Double.parseDouble(val_cost[3])  ) +  Double.parseDouble(val_cost[65])
        //capexNONIT5 =  ( Double.parseDouble(val_cost[48]) - Double.parseDouble(val_cost[4])  ) +  Double.parseDouble(val_cost[66])
      //  capexNONITT =  ( Double.parseDouble(val_cost[49]) - Double.parseDouble(val_cost[5])  ) +  Double.parseDouble(val_cost[67])
		//}
         //*NUEVO PRIOR MFS**//
         capexNONITT = capexNONIT1 + capexNONIT2 + capexNONIT3+ capexNONIT4+ capexNONIT5  + capexNONITprior //PRIOR MFS3
         //*NUEVO PRIOR MFS**//
        //capexNONITT = capexNONIT1 + capexNONIT2 + capexNONIT3+ capexNONIT4+ capexNONIT5 
/**************************************nuevo *******************************/
gridDataRec.each()//por cada Linea de Coste del Grid Recurrentes
{ 
	totrec =totrec+it.totalSolicitado
    ano1 = (ano1+it.solicitadoY1) == null ? 0: ano1+it.solicitadoY1 
    ano2 = (ano2+it.solicitadoY2) == null ? 0: ano2+it.solicitadoY2
    ano3 = (ano3+it.solicitadoY3 ) == null ? 0: ano3+it.solicitadoY3 
    ano4 = (ano4+it.solicitadoY4) == null ? 0: ano4+it.solicitadoY4
    ano5 = (ano5+it.solicitadoY5)  == null ? 0:ano5+it.solicitadoY5
}
gridDataNIT.each()//por cada Linea de Coste del Grid Recurrentes
{ 
	totrec =totrec+it.totalSolicitado
    ano1NIT = ano1+it.solicitadoY1; 
    ano2NIT = ano2+it.solicitadoY2;
    ano3NIT = ano3+it.solicitadoY3;
    ano4NIT = ano4+it.solicitadoY4; 
    ano5NIT = ano5+it.solicitadoY5;
}
/**************************************nuevo *******************************/

	 opexITprior = Double.parseDouble(val_cost[26]) - ( Double.parseDouble(val_cost[81]) + Double.parseDouble(val_cost[82]) ) 
	
     //   if(esProyecto())
		//{
			opexIT1 = val_cost.size() > 132 ? Double.parseDouble(val_cost[132]) :0
			opexIT2 = val_cost.size() > 132 ? Double.parseDouble(val_cost[133]) :0
			opexIT3 = val_cost.size() > 132 ? Double.parseDouble(val_cost[134]) :0
			opexIT4 = val_cost.size() > 132 ? Double.parseDouble(val_cost[135]) :0
			opexIT5 = val_cost.size() > 132 ? Double.parseDouble(val_cost[136]) :0
/**************************************nuevo *******************************/
			opexIT1 = opexIT1+ano1/1000;
			opexIT2 = opexIT2+ano2/1000;
			opexIT3 = opexIT3+ano3/1000;
			opexIT4 = opexIT4+ano4/1000;
			opexIT5 = opexIT5+ano5/1000;
/**************************************nuevo *******************************/

	//	}else if(esProyectoComoServicio())
		//{
			//opexIT1 = Double.parseDouble(val_cost[95])
			//opexIT2 = Double.parseDouble(val_cost[96])
			//opexIT3 = Double.parseDouble(val_cost[97])
			//opexIT4 = Double.parseDouble(val_cost[98])
			//opexIT5 = Double.parseDouble(val_cost[99])
			
	//	}else
	//	{
	//		opexIT1 =  ( Double.parseDouble(val_cost[6]) - (Double.parseDouble(val_cost[50]) + Double.parseDouble(val_cost[56])  ) ) + (    Double.parseDouble(val_cost[20]) - ( Double.parseDouble(val_cost[68]) + Double.parseDouble(val_cost[74])  )    )
	//		opexIT2 =  ( Double.parseDouble(val_cost[7]) - (Double.parseDouble(val_cost[51]) + Double.parseDouble(val_cost[57])  ) ) + (    Double.parseDouble(val_cost[21]) - ( Double.parseDouble(val_cost[69]) + Double.parseDouble(val_cost[75])  )    )
	//		opexIT3 =  ( Double.parseDouble(val_cost[8]) - (Double.parseDouble(val_cost[52]) + Double.parseDouble(val_cost[58])  ) ) + (    Double.parseDouble(val_cost[22]) - ( Double.parseDouble(val_cost[70]) + Double.parseDouble(val_cost[76])  )    )
	//		opexIT4 =  ( Double.parseDouble(val_cost[9]) - (Double.parseDouble(val_cost[53]) + Double.parseDouble(val_cost[59])  ) ) + (    Double.parseDouble(val_cost[23]) - ( Double.parseDouble(val_cost[71]) + Double.parseDouble(val_cost[77])  )    )
	//		opexIT5 =  ( Double.parseDouble(val_cost[10]) - (Double.parseDouble(val_cost[54]) + Double.parseDouble(val_cost[60])  ) ) + (    Double.parseDouble(val_cost[24]) - ( Double.parseDouble(val_cost[72]) + Double.parseDouble(val_cost[78])  )    )
			
	//	}
		//opexITT =  ( Double.parseDouble(val_cost[11]) - (Double.parseDouble(val_cost[55]) + Double.parseDouble(val_cost[61])  ) ) + (    Double.parseDouble(val_cost[14]) - ( Double.parseDouble(val_cost[73]) + Double.parseDouble(val_cost[79])  )    )
        opexITT =  opexIT1 + opexIT2 + opexIT3 + opexIT4 + opexIT5
       // opexNONITprior = Double.parseDouble(val_cost[81])
       
		opexNONIT1 = (ano1NIT/1000) + Double.parseDouble(val_cost[50]) +Double.parseDouble(val_cost[68]);        
		opexNONIT2 = (ano2NIT/1000) + Double.parseDouble(val_cost[51]) +Double.parseDouble(val_cost[69]);
        opexNONIT3 = (ano3NIT/1000) + Double.parseDouble(val_cost[52]) +Double.parseDouble(val_cost[70]);        
		opexNONIT4 = (ano4NIT/1000) + Double.parseDouble(val_cost[53]) +Double.parseDouble(val_cost[71]);
        opexNONIT5 = (ano5NIT/1000) + Double.parseDouble(val_cost[54]) +Double.parseDouble(val_cost[72]);      //  opexNONITT = Double.parseDouble(val_cost[55]) +Double.parseDouble(val_cost[73]);
		
        opexNONITT =opexNONIT1 + opexNONIT2 + opexNONIT3+ opexNONIT4 + opexNONIT5
        opexRECprior = Double.parseDouble(val_cost[82])
        opexREC1 = Double.parseDouble(val_cost[74]) + Double.parseDouble(val_cost[56])
        opexREC2 = Double.parseDouble(val_cost[75]) +Double.parseDouble(val_cost[57])
       opexREC3 = Double.parseDouble(val_cost[76]) +Double.parseDouble(val_cost[58])
        opexREC4 = Double.parseDouble(val_cost[77]) +Double.parseDouble(val_cost[59])
        opexREC5 = Double.parseDouble(val_cost[78]) +Double.parseDouble(val_cost[60])
        /**NUEVO MFS **/
        opexRECT = Double.parseDouble(val_cost[79]) +Double.parseDouble(val_cost[61]) + opexRECprior //PRIOR MFS3
        /**NUEVO MFS **/
       //opexRECT = Double.parseDouble(val_cost[79]) +Double.parseDouble(val_cost[61])
    }
    //MET-897
}else{
    return ""
}
def val_totalCRMC = 0;def val_totalCRMO = 0;def val_totalRLA = 0;def val_totalCAV = 0;def val_revi = 0;def val_costav = 0;def val_rla = 0;def val_crmo  = 0;def val_crmc = 0;double  CRMC1 =0;double  CRMC2 =0
double  CRMC3 =0;double  CRMC4 =0;double  CRMC5 =0;double  CRMO1 =0;double  CRMO2 =0;double  CRMO3 =0;double CRMO4 =0;double  CRMO5 =0;double  RLA1 = 0;double  RLA2 = 0;double  RLA3 = 0;double  RLA4 = 0
double  RLA5 = 0;double  CAV1 = 0;double  CAV2 = 0;double  CAV3 = 0;double  CAV4 = 0;double  CAV5 = 0;double  REI1 = 0;double  REI2 = 0;double  REI3 = 0;double  REI4 = 0;double  REI5 = 0;double reiPD1 =0
double reiPD2 =0;double reiPD3 =0;double reiPD4 =0;double reiPD5 =0
//MET-897
totalCRMOIT = 0;CRMOIT1 = 0;CRMOIT2 = 0;CRMOIT3 = 0;CRMOIT4 = 0;CRMOIT5 = 0;totalCRMONOIT = 0;CRMONOIT1 = 0;CRMONOIT2 = 0;CRMONOIT3 = 0;CRMONOIT4 = 0;CRMONOIT5 = 0;//MET-897
//MET-966
RORI1 =0;RORI2 =0;RORI3 =0;RORI4 =0;RORI5 =0;totalRORI =0;totalRENOPD  = 0;RENOPD1  = 0;RENOPD2  = 0;RENOPD3  = 0;RENOPD4  = 0;RENOPD5  = 0///MET-966
def val_benefit =  (issue.getCustomFieldValue(cf_benefit) != null ? issue.getCustomFieldValue(cf_benefit) : 0 )
if(val_benefit != 0)
{
    val_benefit = val_benefit.replace("[","").replace("]","").split(",")
    val_crmc = val_benefit[6]
    val_crmo = val_benefit[7]
    val_rla =  val_benefit[8]
    val_costav =  val_benefit[9]
    val_revi =    val_benefit[10]
    CRMC1 = val_benefit.size()-1 < 11 ? 0 : Double.parseDouble(val_benefit[11]).round()
    CRMC2 = val_benefit.size()-1 < 12 ? 0 : Double.parseDouble(val_benefit[12]).round()
    CRMC3 = val_benefit.size()-1 < 13 ? 0 : Double.parseDouble(val_benefit[13]).round()
    CRMC4 = val_benefit.size()-1 < 14 ? 0 : Double.parseDouble(val_benefit[14]).round()
    CRMC5 = val_benefit.size()-1 < 15 ? 0 : Double.parseDouble(val_benefit[15]).round()
    CRMO1 = val_benefit.size()-1 < 16 ? 0 : Double.parseDouble(val_benefit[16]).round()
    CRMO2 = val_benefit.size()-1 < 17 ? 0 : Double.parseDouble(val_benefit[17]).round()
    CRMO3 = val_benefit.size()-1 < 18 ? 0 : Double.parseDouble(val_benefit[18]).round()
    CRMO4 = val_benefit.size()-1 < 19 ? 0 : Double.parseDouble(val_benefit[19]).round()
    CRMO5 = val_benefit.size()-1 < 20 ? 0 : Double.parseDouble(val_benefit[20]).round()
    RLA1 =  val_benefit.size()-1 < 21 ? 0 : Double.parseDouble(val_benefit[21]).round()
    RLA2 =  val_benefit.size()-1 < 22 ? 0 : Double.parseDouble(val_benefit[22]).round()
    RLA3 =  val_benefit.size()-1 < 23 ? 0 : Double.parseDouble(val_benefit[23]).round()
    RLA4 =  val_benefit.size()-1 < 24 ? 0 : Double.parseDouble(val_benefit[24]).round()
    RLA5 =  val_benefit.size()-1 < 25 ? 0 : Double.parseDouble(val_benefit[25]).round()
    CAV1 =  val_benefit.size()-1 < 26 ? 0 : Double.parseDouble(val_benefit[26]).round()
    log.error('CAV1-->'+CAV1)
    CAV2 =  val_benefit.size()-1 < 27 ? 0 : Double.parseDouble(val_benefit[27]).round()
    CAV3 =  val_benefit.size()-1 < 28 ? 0 : Double.parseDouble(val_benefit[28]).round()
    CAV4 =  val_benefit.size()-1 < 29 ? 0 : Double.parseDouble(val_benefit[29]).round()
    CAV5 =  val_benefit.size()-1 < 30 ? 0 : Double.parseDouble(val_benefit[30]).round()
    REI1 =  val_benefit.size()-1 < 31 ? 0 : Double.parseDouble(val_benefit[31]).round()
    REI2 =  val_benefit.size()-1 < 32 ? 0 : Double.parseDouble(val_benefit[32]).round()
    REI3 =  val_benefit.size()-1 < 33 ? 0 : Double.parseDouble(val_benefit[33]).round()
    REI4 =  val_benefit.size()-1 < 34 ? 0 : Double.parseDouble(val_benefit[34]).round()
    REI5 =  val_benefit.size()-1 < 35 ? 0 : Double.parseDouble(val_benefit[35]).round()
    reiPD1 =  val_benefit.size()-1 < 36 ? 0 : Double.parseDouble(val_benefit[36]).round()
    reiPD2 =  val_benefit.size()-1 < 37 ? 0 : Double.parseDouble(val_benefit[37]).round()
    reiPD3 =  val_benefit.size()-1 < 38 ? 0 : Double.parseDouble(val_benefit[38]).round()
    reiPD4 =  val_benefit.size()-1 < 39 ? 0 : Double.parseDouble(val_benefit[39]).round()
    reiPD5 =  val_benefit.size()-1 < 40 ? 0 : Double.parseDouble(val_benefit[40]).round()
    reiPD1new =  0;    reiPD2new =  0;    reiPD3new =  0;    reiPD4new =  0;    reiPD5new =  0
    CRMO1new = val_benefit.size()-1 < 16 ? 0 : Double.parseDouble(val_benefit[16]).round()
    CRMO2new = val_benefit.size()-1 < 17 ? 0 : Double.parseDouble(val_benefit[17]).round()
    CRMO3new = val_benefit.size()-1 < 18 ? 0 : Double.parseDouble(val_benefit[18]).round()
    CRMO4new = val_benefit.size()-1 < 19 ? 0 : Double.parseDouble( val_benefit[19]).round()
    CRMO5new = val_benefit.size()-1 < 20 ? 0 : Double.parseDouble(val_benefit[20]).round()
    if(val_benefit.size()-1 > 42){
        reiPD1new =   Double.parseDouble(val_benefit[42]).round()
        reiPD2new =   Double.parseDouble(val_benefit[43]).round()
        reiPD3new =   Double.parseDouble(val_benefit[44]).round()
        reiPD4new =   Double.parseDouble(val_benefit[45]).round()
        reiPD5new =   Double.parseDouble(val_benefit[46]).round()
        CRMO1new =  Double.parseDouble(val_benefit[47]).round()
        CRMO2new = Double.parseDouble(val_benefit[48]).round()
        CRMO3new =  Double.parseDouble(val_benefit[49]).round()
        CRMO4new =  Double.parseDouble( val_benefit[50]).round()
        CRMO5new = Double.parseDouble(val_benefit[51]).round()
    }
//MET-897
    if(val_benefit.size()-1 > 60){
        totalCRMOIT = Double.parseDouble(val_benefit[58]).round()
        CRMOIT1 = Double.parseDouble(val_benefit[53]).round()
        CRMOIT2 = Double.parseDouble(val_benefit[54]).round()
        CRMOIT3 = Double.parseDouble(val_benefit[55]).round()
        CRMOIT4 = Double.parseDouble(val_benefit[56]).round()
        CRMOIT5 = Double.parseDouble(val_benefit[57]).round()
        totalCRMONOIT = Double.parseDouble(val_benefit[64]).round()
        CRMONOIT1 = Double.parseDouble(val_benefit[59]).round()
        CRMONOIT2 = Double.parseDouble(val_benefit[60]).round()
        CRMONOIT3 = Double.parseDouble(val_benefit[61]).round()
        CRMONOIT4 = Double.parseDouble(val_benefit[62]).round()
        CRMONOIT5 = Double.parseDouble(val_benefit[63]).round()
    }
    //MET-966
    if(val_benefit.size()-1 > 65){
        totalRORI = Double.parseDouble(val_benefit[71]).round()
        RORI1 = Double.parseDouble(val_benefit[66]).round()
        RORI2 = Double.parseDouble(val_benefit[67]).round()
        RORI3 = Double.parseDouble(val_benefit[68]).round()
        RORI4 = Double.parseDouble(val_benefit[69]).round()
        RORI5 = Double.parseDouble(val_benefit[70]).round()
        RENOPD1 = Double.parseDouble(val_benefit[72]).round()
        RENOPD2 = Double.parseDouble(val_benefit[73]).round()
        RENOPD3 = Double.parseDouble(val_benefit[74]).round()
        RENOPD4 = Double.parseDouble(val_benefit[75]).round()
        RENOPD5 = Double.parseDouble(val_benefit[76]).round()
    }
}
def val_cash = 0
def val_cash1 =  0
def val_cash2 = 0
def val_cash3 = 0
def val_cash4 = 0
def val_cash5 = 0

//if(val_benefit != 0 || val_cost != 0 )
//{

    if(val_cost.size() > 89)
    {
        
        if(val_cost == 0 &&  val_benefit != 0 )
        {
            val_cash1 = Math.floor(Double.parseDouble(val_benefit[0]).round(2)/1000 ).intValue()
            val_cash2 = Math.floor(Double.parseDouble(val_benefit[1]).round(2)/1000 ).intValue()
            val_cash3 = Math.floor(Double.parseDouble(val_benefit[2]).round(2)/1000 ).intValue()
            val_cash4 = Math.floor(Double.parseDouble(val_benefit[3]).round(2)/1000 ).intValue()
            val_cash5 = Math.floor(Double.parseDouble(val_benefit[4]).round(2)/1000 ).intValue()	
			log.error('CASH1')
            
        }
        
         
        if(val_cost != 0 &&  val_benefit == 0 )
        {

            //val_cash1 = Math.floor(0.0D - ( Double.parseDouble(val_cost[89]) + ( Double.parseDouble(val_cost[107])   ) + Double.parseDouble(val_cost[15]) +Double.parseDouble( val_cost[101]) + Double.parseDouble(val_cost[20]) ).round(2)).intValue()
			val_cash2 = Math.floor(0.0D - ( Double.parseDouble(val_cost[90])+  ( Double.parseDouble(val_cost[108])   ) + Double.parseDouble(val_cost[16]) +Double.parseDouble( val_cost[102]) + Double.parseDouble(val_cost[21])).round(2)).intValue()
            val_cash3 = Math.floor(0.0D - ( Double.parseDouble(val_cost[91])+  ( Double.parseDouble(val_cost[109])   ) + Double.parseDouble(val_cost[17]) +Double.parseDouble( val_cost[103]) + Double.parseDouble(val_cost[22])).round(2)).intValue()
            val_cash4 = Math.floor(0.0D - ( Double.parseDouble(val_cost[92])+  ( Double.parseDouble(val_cost[110])   ) + Double.parseDouble(val_cost[18]) +Double.parseDouble( val_cost[104]) + Double.parseDouble(val_cost[23])).round(2)).intValue()
            val_cash5 = Math.floor(0.0D - ( Double.parseDouble(val_cost[93])+  ( Double.parseDouble(val_cost[111])   ) + Double.parseDouble(val_cost[19]) +Double.parseDouble( val_cost[105]) + Double.parseDouble(val_cost[24])).round(2)).intValue()
/**********************************CAMBIO POR FALLO DE CASH FLOW************************************************/		
            val_cash1 = Math.floor(0.0D - ( (Double.parseDouble(val_cost[89]) ) + ( Double.parseDouble(val_cost[107])   ) + Double.parseDouble(val_cost[15]) +Double.parseDouble( val_cost[101]))).intValue()
            //val_cash2 = Math.floor(0.0D - ( Double.parseDouble(val_cost[90])+  ( Double.parseDouble(val_cost[108])   ) + Double.parseDouble(val_cost[16]) +Double.parseDouble( val_cost[102]))).intValue()
            //val_cash3 = Math.floor(0.0D - ( Double.parseDouble(val_cost[91])+  ( Double.parseDouble(val_cost[109])   ) + Double.parseDouble(val_cost[17]) +Double.parseDouble( val_cost[103]))).intValue()
            //val_cash4 = Math.floor(0.0D - ( Double.parseDouble(val_cost[92])+  ( Double.parseDouble(val_cost[110])   ) + Double.parseDouble(val_cost[18]) +Double.parseDouble( val_cost[104]))).intValue()
            //val_cash5 = Math.floor(0.0D - ( Double.parseDouble(val_cost[93])+  ( Double.parseDouble(val_cost[111])   ) + Double.parseDouble(val_cost[19]) +Double.parseDouble( val_cost[105]))).intValue()
			log.error('CASH2')
       		log.error('val_benefit->'+val_benefit)
/**********************************CAMBIO POR FALLO DE CASH FLOW************************************************/		


        }
     
        if(val_benefit != 0 && val_cost != 0 )
        {
            val_cash1 = Math.floor(Double.parseDouble(val_benefit[0]).round(2)/1000 - ( Double.parseDouble(val_cost[89]) + ( Double.parseDouble(val_cost[107])   ) + Double.parseDouble(val_cost[15]) +Double.parseDouble( val_cost[101]) + Double.parseDouble(val_cost[20]) ).round(2)).intValue()
            val_cash2 = Math.floor(Double.parseDouble(val_benefit[1]).round(2)/1000 - ( Double.parseDouble(val_cost[90])+  ( Double.parseDouble(val_cost[108])   ) + Double.parseDouble(val_cost[16]) +Double.parseDouble( val_cost[102]) + Double.parseDouble(val_cost[21])).round(2)).intValue() 
            val_cash3 = Math.floor(Double.parseDouble(val_benefit[2]).round(2)/1000 - ( Double.parseDouble(val_cost[91])+  ( Double.parseDouble(val_cost[109])   ) + Double.parseDouble(val_cost[17]) +Double.parseDouble( val_cost[103]) + Double.parseDouble(val_cost[22])).round(2)).intValue()
            val_cash4 = Math.floor(Double.parseDouble(val_benefit[3]).round(2)/1000 - ( Double.parseDouble(val_cost[92])+  ( Double.parseDouble(val_cost[110])   ) + Double.parseDouble(val_cost[18]) +Double.parseDouble( val_cost[104]) + Double.parseDouble(val_cost[23])).round(2)).intValue()
            val_cash5 = Math.floor(Double.parseDouble(val_benefit[4]).round(2)/1000 - ( Double.parseDouble(val_cost[93])+  ( Double.parseDouble(val_cost[111])   ) + Double.parseDouble(val_cost[19]) +Double.parseDouble( val_cost[105]) + Double.parseDouble(val_cost[24])).round(2)).intValue()
			def sumCost1 = ((Double.parseDouble(val_cost[89]) + ( Double.parseDouble(val_cost[107])   ) + Double.parseDouble(val_cost[15]) +Double.parseDouble( val_cost[101]) + Double.parseDouble(val_cost[20]) ).round(2)).intValue()
			def sumCost2 = ((Double.parseDouble(val_cost[90])+  ( Double.parseDouble(val_cost[108])   ) + Double.parseDouble(val_cost[16]) +Double.parseDouble( val_cost[102]) + Double.parseDouble(val_cost[21])).round(2)).intValue()
			def sumCost3 = ((Double.parseDouble(val_cost[91])+  ( Double.parseDouble(val_cost[109])   ) + Double.parseDouble(val_cost[17]) +Double.parseDouble( val_cost[103]) + Double.parseDouble(val_cost[22])).round(2)).intValue()
			def sumCost4 = ((Double.parseDouble(val_cost[92])+  ( Double.parseDouble(val_cost[110])   ) + Double.parseDouble(val_cost[18]) +Double.parseDouble( val_cost[104]) + Double.parseDouble(val_cost[23])).round(2)).intValue()
			def sumCost5 = ((Double.parseDouble(val_cost[93])+  ( Double.parseDouble(val_cost[111])   ) + Double.parseDouble(val_cost[19]) +Double.parseDouble( val_cost[105]) + Double.parseDouble(val_cost[24])).round(2)).intValue()
			/*if (sumCost1 > 0)  val_cash1 = val_cash1.intdiv(sumCost1)
			if (sumCost2 > 0)val_cash2 = val_cash2.intdiv(sumCost2)
			if (sumCost3 > 0)val_cash3 = val_cash3.intdiv(sumCost3)
			if (sumCost4 > 0)val_cash4 = val_cash4.intdiv(sumCost4)
			if (sumCost5 > 0)val_cash5 = val_cash5.intdiv(sumCost5)*/
			log.error('CASH3')
			log.warn('val_benefit -->'+val_benefit)
			val_cash1 = ((capexIT1+capexNONIT1+opexIT1+opexNONIT1)-(opexREC1+(CRMC1.intValue())+(CRMO1.intValue()) + RORI1 + (RLA1.intValue()) )/1000 ).intValue() 
            val_cash2 = ((capexIT2+capexNONIT2+opexIT2+opexNONIT2)-(opexREC2+(CRMC2.intValue())+(CRMO2.intValue()) + RORI2 + (RLA2.intValue()) )/1000 ).intValue()
            val_cash3 = ((capexIT3+capexNONIT3+opexIT3+opexNONIT3)-(opexREC3+(CRMC3.intValue())+(CRMO3.intValue()) + RORI3 + (RLA3.intValue()) )/1000 ).intValue()
            val_cash4 = ((capexIT4+capexNONIT4+opexIT4+opexNONIT4)-(opexREC4+(CRMC4.intValue())+(CRMO4.intValue()) + RORI4 + (RLA4.intValue()) )/1000 ).intValue()
            val_cash5 = ((capexIT5+capexNONIT5+opexIT5+opexNONIT5)-(opexREC5+(CRMC5.intValue())+(CRMO5.intValue()) + RORI5 + (RLA5.intValue()) )/1000 ).intValue()
			log.error('cashflow2-->'+val_cash2)
			//+', val_cost[108]-->'+val_cost[16]+', val_cost[102] 21
		}
      
		val_cash = val_cash1 + val_cash2 + val_cash3 + val_cash4 + val_cash5
    }
//}

def cf_kpi = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(KPI)
def cf_hito = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(HITO)
def cf_be = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(BE)
def cf_opexim = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(OPEXIM)
def cf_amort = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(AMORT)
def cf_total = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(TOTAL)
def cf_namort = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(NAMORT)
def cf_ramort = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(RAMORT)
def cf_eff = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(EFF)
def val_be = ( issue.getCustomFieldValue(cf_be) != null && issue.getCustomFieldValue(cf_be) != "" ?  issue.getCustomFieldValue(cf_be) : "" )
def val_opexim = ( issue.getCustomFieldValue(cf_opexim) != null && issue.getCustomFieldValue(cf_opexim) != "" ?  issue.getCustomFieldValue(cf_opexim) : 0 )
def val_amort = ( issue.getCustomFieldValue(cf_amort) != null && issue.getCustomFieldValue(cf_amort) != "" ?  issue.getCustomFieldValue(cf_amort) : "" )
//def val_total = ( issue.getCustomFieldValue(cf_total) != null && issue.getCustomFieldValue(cf_total) != "" ?  issue.getCustomFieldValue(cf_total) : "" )
def val_namort = ( issue.getCustomFieldValue(cf_namort) != null && issue.getCustomFieldValue(cf_namort) != "" ?  issue.getCustomFieldValue(cf_namort) : "" )
def val_ramort = ( issue.getCustomFieldValue(cf_ramort) != null && issue.getCustomFieldValue(cf_ramort) != "" ?  issue.getCustomFieldValue(cf_ramort) : "" )
def val_eff = ( issue.getCustomFieldValue(cf_eff) != null && issue.getCustomFieldValue(cf_eff) != "" ?  issue.getCustomFieldValue(cf_eff) : "" )
def val_irr = ( issue.getCustomFieldValue(cf_irr) != null && issue.getCustomFieldValue(cf_irr) != "" ?  issue.getCustomFieldValue(cf_irr) : "" )
def val_opexim1 = (Double.parseDouble(val_opex1Num.toString()) - Double.parseDouble(CRMO1.toString())).intValue()
def val_opexim2 = (Double.parseDouble(val_opex2Num.toString()) - Double.parseDouble(CRMO2.toString())).intValue()
def val_opexim3 = (Double.parseDouble(val_opex3Num.toString()) - Double.parseDouble(CRMO3.toString())).intValue()
def val_opexim4 = (Double.parseDouble(val_opex4Num.toString()) - Double.parseDouble(CRMO4.toString())).intValue()
def val_opexim5 = (Double.parseDouble(val_opex5Num.toString()) - Double.parseDouble(CRMO5.toString())).intValue()
def val_total   = (Double.parseDouble(val_revi.toString()) - Double.parseDouble(val_opexim.toString()) - Double.parseDouble(val_amort)).intValue()
def val_total1  = (Double.parseDouble(REI1.toString()) - val_opexim1 - (newAmort1 - reducAmort1)).intValue()
def val_total2 =  (Double.parseDouble(REI2.toString()) - val_opexim2 - (newAmort2 - reducAmort2)).intValue()
def val_total3 =  (Double.parseDouble(REI3.toString()) - val_opexim3 - (newAmort3 - reducAmort3)).intValue()
def val_total4 =  (Double.parseDouble(REI4.toString()) - val_opexim4 - (newAmort4 - reducAmort4)).intValue()
def val_total5 =  (Double.parseDouble(REI5.toString()) - val_opexim5 - (newAmort5 - reducAmort5)).intValue()
def val_capex_html = (val_capex.class == Double ? val_capex.intValue() :  val_capex.class == Integer ? val_capex : Double.parseDouble(val_capex).intValue())
def val_opex_html =  (val_opex.class == Double ? val_opex.intValue() : val_opex.class == Integer ? val_opex : Double.parseDouble(val_opex).intValue())
def val_crmc_html =  (val_crmc.class == Double ? val_crmc.intValue() : val_crmc.class == Integer ? val_crmc : Double.parseDouble(val_crmc).intValue())
def val_crmo_html =  (val_crmo.class == Double ? val_crmo.intValue() : val_crmo.class == Integer ? val_crmo : Double.parseDouble(val_crmo).intValue())
def val_rla_html =   (val_rla.class == Double ?  val_rla.intValue() : val_rla.class == Integer ? val_rla :    Double.parseDouble(val_rla).intValue() )
def val_costav_html =(val_costav.class == Double ? val_costav.intValue() : val_costav.class == Integer ? val_costav   : Double.parseDouble(val_costav).intValue())
def val_revi_html = (val_revi.class == Double ? val_revi.intValue() :  val_revi.class == Integer ?       val_revi :     Double.parseDouble(val_revi).intValue())
def val_cash_html = (val_cash.class == Double ? val_cash.intValue() :  val_cash.class ==Integer ?  val_cash  :          Double.parseDouble(val_cash.toString()).intValue())

def result=    """
<table   border='0' cellpadding='0' cellspacing='0' style='border-collapse: collapse;table-layout: fixed; width: 80%; margin: 0em; font-family: Arial, Helvetica, sans-serif; font-size: 0.80em;'>
  <tbody>
    <tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;'>
    <td style='padding-top: 25px;width: 30%;font-weight: bold;'></td>
	<td style='width: 14%;font-weight: bold;'></td>
	<td style='width: 14%;font-weight: bold;'></td>
	<td style='width: 14%;font-weight: bold;'></td>
	<td style='width: 14%;font-weight: bold;'></td>
	<td style='width: 14%;font-weight: bold;'></td>
 	 <td style='width: 14%;font-weight: bold;font-size: 13px;'></td>
      <td style='width: 14%;font-weight: bold; text-align: right;'></td></tr>
	<tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;background:#f2f2f2;'>
      <td style='width: 30%;font-weight: bold;font-size: 12px;'>Propuesta Proyecto (k)</td>
	  <td style='width: 12%;font-weight: bold;text-align: right;'>Ant.</td>
	  <td style='width: 12%;font-weight: bold;text-align: right;'>"""+cal.get(Calendar.YEAR)+"""</td>
	  <td style='width: 12%;font-weight: bold;text-align: right;'>"""+(cal.get(Calendar.YEAR)+1)+"""</td>
	  <td style='width: 12%;font-weight: bold;text-align: right;'>"""+(cal.get(Calendar.YEAR)+2)+"""</td>
	  <td style='width: 12%;font-weight: bold; text-align: right;'>"""+(cal.get(Calendar.YEAR)+3)+"""</td>
	  <td style='width: 12%;font-weight: bold; text-align: right;'>"""+(cal.get(Calendar.YEAR)+4)+"""</td>
      <td style='width: 12%;font-weight: bold;text-align: right;'>Total</td>
	</tr>"""
result = result +
        """
	<tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;'>
      <td style='font-size: 13px;'>CAPEX IT</td>
	  <td style='width: 30%;text-align: right;""" +(capexITprior  < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(capexITprior )+"""</td>
	  <td style='width: 12%;text-align: right;""" +(capexIT1  < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(capexIT1)+"""</td>
	  <td style='width: 12%;text-align: right;""" +(capexIT2  < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(capexIT2)+"""</td>
	  <td style='width: 12%;text-align: right;""" +(capexIT3  < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(capexIT3)+"""</td>
	  <td style='width: 12%;text-align: right;""" +(capexIT4  < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(capexIT4)+"""</td>
	  <td style='width: 12%;text-align: right;""" +(capexIT5  < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(capexIT5)+"""</td>
      <td style='width: 12%;text-align: right;font-weight: bold;""" +(capexITT  < 0?"color : red;" : "")+""" '>"""+NumberFormat.getIntegerInstance().format(capexITT)+"""</td> 
    </tr>
    <tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;'>
      <td style='font-size: 13px;'>CAPEX NO IT</td>
	  <td style='width: 30%;text-align: right;""" +(capexNONITprior < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(capexNONITprior)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(capexNONIT1  < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(capexNONIT1)+"""</td>
	  <td style='width: 12%;text-align: right;""" +(capexNONIT2 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(capexNONIT2)+"""</td>
	  <td style='width: 12%;text-align: right;""" +(capexNONIT3 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(capexNONIT3)+"""</td>
	  <td style='width: 12%;text-align: right;""" +(capexNONIT4 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(capexNONIT4)+"""</td>
	  <td style='width: 12%;text-align: right;""" +(capexNONIT5 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(capexNONIT5)+"""</td>
      <td style='width: 12%;text-align: right;font-weight: bold;""" +(capexNONITT < 0?"color : red;" : "")+""" '>"""+NumberFormat.getIntegerInstance().format(capexNONITT)+"""</td> 
    </tr>
    <tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;'>
      <td style='width: 30%;font-size: 13px;'>OPEX IT</td>
      <td style='width: 12%; text-align: right;""" +(opexITprior < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexITprior)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(opexIT1 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexIT1)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(opexIT2 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexIT2)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(opexIT3 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexIT3)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(opexIT4 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexIT4)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(opexIT5 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexIT5)+"""</td>
	  <td style='width: 12%; text-align: right;font-weight: bold;""" +(opexITT < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexITT)+"""</td>
    </tr>
   <tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;'>
      <td style='width: 30%;font-size: 13px;'>OPEX NO IT</td>
      <td style='width: 12%; text-align: right;""" +(opexNONITprior < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexNONITprior)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(opexNONIT1 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexNONIT1)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(opexNONIT2 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexNONIT2)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(opexNONIT3 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexNONIT3)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(opexNONIT4 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexNONIT4)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(opexNONIT5 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexNONIT5)+"""</td>
	  <td style='width: 12%; text-align: right;font-weight: bold;""" +(opexNONITT < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexNONITT)+"""</td>
    </tr>
     <tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;'>
      <td style='width: 30%;font-size: 13px;'>OPEX Recursos int.</td>
      <td style='width: 12%; text-align: right;""" +(opexRECprior < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexRECprior)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(opexREC1 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexREC1)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(opexREC2< 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexREC2)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(opexREC3 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexREC3)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(opexREC4 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexREC4)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(opexREC5 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexREC5)+"""</td>
	  <td style='width: 12%; text-align: right;font-weight: bold;""" +(opexRECT < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexRECT)+"""</td>
    </tr>
    """
result = result +
      """
	<tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;'>
      <td style='width: 30%;'>Reduc. de Costes </td>
	  <td style='width: 12%; text-align: right;'>0</td>
	  <td style='width: 12%; text-align: right;""" +(CRMC1.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(CRMC1.intValue()/1000)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(CRMC2.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(CRMC2.intValue()/1000)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(CRMC3.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(CRMC3.intValue()/1000)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(CRMC4.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(CRMC4.intValue()/1000)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(CRMC5.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(CRMC5.intValue()/1000)+"""</td>
      <td style='width: 12%; text-align: right;font-weight: bold;""" +(val_crmc_html.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(val_crmc_html/1000)+"""</td>
    </tr>
    """
//MET-897
/*if(val_cost.size()-1 > 60) //modelo con it/non it en la reducc. opex
{
    result = result +        """
		<tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;'>
      <td style='width: 30%;'>Reduc. OPEX IT </td>
	  <td style='width: 12%; text-align: right;'>0</td>
	  <td style='width: 12%; text-align: right;""" +(CRMOIT1.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(CRMOIT1.intValue())+"""</td>
	   <td style='width: 12%; text-align: right;""" +(CRMOIT2.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(CRMOIT2.intValue())+"""</td>
	    <td style='width: 12%; text-align: right;""" +(CRMOIT3.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(CRMOIT3.intValue())+"""</td>
		 <td style='width: 12%; text-align: right;""" +(CRMOIT4.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(CRMOIT4.intValue())+"""</td>
		  <td style='width: 12%; text-align: right;""" +(CRMOIT5.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(CRMOIT5.intValue())+"""</td>
      <td style=' text-align: right;font-weight: bold;""" +(totalCRMOIT.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(totalCRMOIT.intValue())+"""</td>
    </tr>
   <tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;'>
      <td style='width: 30%;'>Reduc. OPEX NO IT</td>
	  <td style='width: 12%; text-align: right;'>0</td>
	  <td style='width: 12%; text-align: right;""" +(CRMONOIT1.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(CRMONOIT1.intValue())+"""</td>
	   <td style='width: 12%; text-align: right;""" +(CRMONOIT2.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(CRMONOIT2.intValue())+"""</td>
	    <td style='width: 12%; text-align: right;""" +(CRMONOIT3.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(CRMONOIT3.intValue())+"""</td>
		 <td style='width: 12%; text-align: right;""" +(CRMONOIT4.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(CRMONOIT4.intValue())+"""</td>
		  <td style='width: 12%; text-align: right;""" +(CRMONOIT5.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(CRMONOIT5.intValue())+"""</td>
      <td style=' text-align: right;font-weight: bold;""" +(totalCRMONOIT.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(totalCRMONOIT.intValue())+"""</td>
    </tr>		"""

}*/
//else{

    result = result +
            """
		<tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;'>
      <td style='width: 30%;'>Reducción de OPEX </td>
	  <td style='width: 12%; text-align: right;'>0</td>
	  <td style='width: 12%; text-align: right;""" +(CRMO1.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(CRMO1.intValue()/1000)+"""</td>
	   <td style='width: 12%; text-align: right;""" +(CRMO2.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(CRMO2.intValue()/1000)+"""</td>
	    <td style='width: 12%; text-align: right;""" +(CRMO3.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(CRMO3.intValue()/1000)+"""</td>
		 <td style='width: 12%; text-align: right;""" +(CRMO4.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(CRMO4.intValue()/1000)+"""</td>
		  <td style='width: 12%; text-align: right;""" +(CRMO5.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(CRMO5.intValue()/1000)+"""</td>
      <td style=' text-align: right;font-weight: bold;""" +(val_crmo_html.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(val_crmo_html/1000)+"""</td>
    </tr>

		"""
//}
//MET-966
result = result +
        """
	<tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;'>
      <td style='width: 30%;'>Reduc. OPEX Rec. Int.</td>
	  <td style='width: 12%; text-align: right;'>0</td>
	  <td style='width: 12%; text-align: right;""" +(RORI1 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(RORI1/1000)+"""</td>
	   <td style='width: 12%; text-align: right;""" +(RORI2 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(RORI2/1000)+"""</td>
	    <td style='width: 12%; text-align: right;""" +(RORI3 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(RORI3/1000)+"""</td>
		 <td style='width: 12%; text-align: right;""" +(RORI4 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(RORI4/1000)+"""</td>
		  <td style='width: 12%; text-align: right;""" +(RORI5 < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(RORI5/1000)+"""</td>
      <td style=' text-align: right;font-weight: bold;""" +(totalRORI < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(totalRORI/1000)+"""</td>
    </tr>
	"""
result = result +

        """
	<tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;'>
      <td style='width: 30%;font-size: 12px;'>Perdida ingresos evitada</td>
	  <td style='width: 12%; text-align: right;'>0</td>
	  <td style='width: 12%; text-align: right;""" +(RLA1.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(RLA1.intValue()/1000)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(RLA2.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(RLA2.intValue()/1000)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(RLA3.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(RLA3.intValue()/1000)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(RLA4.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(RLA4.intValue()/1000)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(RLA5.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(RLA5.intValue()/1000)+"""</td>
      <td style='width: 12%; text-align: right;font-weight: bold;""" +(val_rla_html.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(val_rla_html/1000)+"""</td>
    </tr>
     
	<tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;'>
      <td style='width: 30%;'>Costes evitados </td>
	  <td style='width: 12%; text-align: right;'>0</td>
	  <td style='width: 12%; text-align: right;""" +(CAV1.intValue() < 0?"color : red;" : "")+""""'>"""+NumberFormat.getIntegerInstance().format(CAV1.intValue()/1000)+"""</td>
	   <td style='width: 12%; text-align: right;""" +(CAV2.intValue() < 0?"color : red;" : "")+""""'>"""+NumberFormat.getIntegerInstance().format(CAV2.intValue()/1000)+"""</td>
	    <td style='width: 12%; text-align: right;""" +(CAV3.intValue() < 0?"color : red;" : "")+""""'>"""+NumberFormat.getIntegerInstance().format(CAV3.intValue()/1000)+"""</td>
		 <td style='width: 12%; text-align: right;""" +(CAV4.intValue() < 0?"color : red;" : "")+""""'>"""+NumberFormat.getIntegerInstance().format(CAV4.intValue()/1000)+"""</td>
		  <td style='width: 12%; text-align: right;""" +(CAV5.intValue() < 0?"color : red;" : "")+""""'>"""+NumberFormat.getIntegerInstance().format(CAV5.intValue()/1000)+"""</td>
      <td style='width: 12%; text-align: right;font-weight: bold;""" +(val_costav_html.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(val_costav_html/1000)+"""</td>
    </tr>
 	<tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;'>
      <td style='width: 30%;'>Aumento de Ingresos </td>
	   <td style='width: 12%; text-align: right;'>0</td>
	   <td style='width: 12%; text-align: right;""" +(REI1.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(REI1.intValue()/1000)+"""</td>
	   <td style='width: 12%; text-align: right;""" +(REI2.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(REI2.intValue()/1000)+"""</td>
	   <td style='width: 12%; text-align: right;""" +(REI3.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(REI3.intValue()/1000)+"""</td>
	   <td style='width: 12%; text-align: right;""" +(REI4.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(REI4.intValue()/1000)+"""</td>
	   <td style='width: 12%; text-align: right;""" +(REI5.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(REI5.intValue()/1000)+"""</td>
      <td style='width: 12%; text-align: right;font-weight: bold;""" +(val_revi_html.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(val_revi_html/1000)+"""</td>
    </tr>
	<tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;background:#f2f2f2;'>
      <td style='width: 30%;font-weight: bold;'>Cash Flow </td>
	  <td style='width: 12%;font-weight: bold;text-align: right;""" +(0 -(capexITprior+capexNONITprior+opexITprior+opexNONITprior+opexRECprior).intValue() < 0?"color : red;" : "")+"""'>"""+ NumberFormat.getIntegerInstance().format(0 -(capexITprior+capexNONITprior+opexITprior+opexNONITprior+opexRECprior))+"""</td>
	  <td style='width: 12%;font-weight: bold; text-align: right;""" +(val_cash1.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(val_cash1)+"""</td>
	  <td style='width: 12%;font-weight: bold; text-align: right;""" +(val_cash2.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(val_cash2)+"""</td>
	  <td style='width: 12%;font-weight: bold;text-align: right;""" +(val_cash3.intValue() < 0?"color : red;" : "")+""" '>"""+NumberFormat.getIntegerInstance().format(val_cash3)+"""</td>
	  <td style='width: 12%;font-weight: bold; text-align: right;""" +(val_cash4.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(val_cash4)+"""</td>
	  <td style='width: 12%;font-weight: bold; text-align: right;""" +(val_cash5.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(val_cash5)+"""</td>
      <td style='width: 12%;font-weight: bold; text-align: right;font-weight: bold;""" +(val_cash_html.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(val_cash_html -(capexITprior+capexNONITprior+opexITprior+opexNONITprior+opexRECprior)  )+"""</td>
    </tr>
 </tbody>
</table>

<table  border='0' cellpadding='0' cellspacing='0' style='table-layout: fixed;border-collapse: collapse; width: 80%; margin-top: 1.5em; font-family: Arial, Helvetica, sans-serif; font-size: 0.80em;'>
  <tbody>
  <tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;background:#f2f2f2;'>
      <td style='width: 30%;font-weight: bold; '>Variación en el PD (k)</td>
	   <td style='width: 14%;font-weight: bold; text-align: right;'>Ant.</td>
	    <td style='width: 14%;font-weight: bold; text-align: right; '>"""+cal.get(Calendar.YEAR)+"""</td>
		 <td style='width: 14%; font-weight: bold; text-align: right;'>"""+(cal.get(Calendar.YEAR)+1)+"""</td>
		  <td style='width: 14%;font-weight: bold; text-align: right; '>"""+(cal.get(Calendar.YEAR)+2)+"""</td>
		   <td style='width: 14%; font-weight: bold; text-align: right;'>"""+(cal.get(Calendar.YEAR)+3)+"""</td>
		    <td style='width: 14%;font-weight: bold;text-align: right; '>"""+(cal.get(Calendar.YEAR)+4)+"""</td>
      <td style='width: 14%; font-weight: bold;text-align: right;'>Total</td>
	</tr>
   
	<tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;'>
      <td style='width: 30%;'>Variación de Ingresos</td>
	  <td style='width: 12%; text-align: right;'> 0 </td>
	  <td style='width: 12%; text-align: right;""" +(reiPD1.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(reiPD1.intValue()/1000)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(reiPD2.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(reiPD2.intValue()/1000)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(reiPD3.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(reiPD3.intValue()/1000)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(reiPD4.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(reiPD4.intValue()/1000)+"""</td>
	  <td style='width: 12%; text-align: right;""" +(reiPD5.intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(reiPD5.intValue()/1000)+"""</td>
      <td style='width: 12%; text-align: right;font-weight: bold;""" +((reiPD1+reiPD2+reiPD3+reiPD4+reiPD5).intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format((   (reiPD1+reiPD2+reiPD3+reiPD4+reiPD5)/1000    ).intValue())+"""</td>
    </tr>
"""
if(val_benefit != 0)
{
    result= result +
            """
	<tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;'>
      <td style='width: 30%;'>Variación OPEX </td>
	  <td style='width: 12%; text-align: right;'>0</td>
	  <td style='width: 12%; text-align: right;"""+(opexIPD1.intValue()- Double.parseDouble(RENOPD1.toString()).intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexIPD1.intValue()- Double.parseDouble(RENOPD1.toString()).intValue()/1000    )+"""</td>
	  <td style='width: 12%; text-align: right;"""+(opexIPD2.intValue()- Double.parseDouble(RENOPD2.toString()).intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexIPD2.intValue()- Double.parseDouble(RENOPD2.toString()).intValue()/1000 )+"""</td>
	  <td style='width: 12%; text-align: right;"""+(opexIPD3.intValue()- Double.parseDouble(RENOPD3.toString()).intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexIPD3.intValue()- Double.parseDouble(RENOPD3.toString()).intValue()/1000)+"""</td>
	  <td style='width: 12%; text-align: right;"""+(opexIPD4.intValue()- Double.parseDouble(RENOPD4.toString()).intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexIPD4.intValue()- Double.parseDouble(RENOPD4.toString()).intValue()/1000)+"""</td>
	  <td style='width: 12%; text-align: right;"""+(opexIPD5.intValue()- Double.parseDouble(RENOPD5.toString()).intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(opexIPD5.intValue()- Double.parseDouble(RENOPD5.toString()).intValue()/1000)+"""</td>
      <td style='width: 12%; text-align: right;font-weight: bold;"""+( (opexIPD1- Double.parseDouble(RENOPD1.toString()).intValue())+ (opexIPD2- Double.parseDouble(RENOPD2.toString()).intValue())
            + (opexIPD3- Double.parseDouble(RENOPD3.toString()).intValue()/1000) + (opexIPD4- Double.parseDouble(RENOPD4.toString()).intValue()/1000) + (opexIPD5- Double.parseDouble(RENOPD5.toString()).intValue()/1000)  < 0 ? "color : red;" : ""  )  +"""'>"""+NumberFormat.getIntegerInstance().format(( (opexIPD1- Double.parseDouble(RENOPD1.toString()).intValue()/1000)+ (opexIPD2- Double.parseDouble(RENOPD2.toString()).intValue()/1000)
            + (opexIPD3- Double.parseDouble(RENOPD3.toString()).intValue()/1000) + (opexIPD4- Double.parseDouble(RENOPD4.toString()).intValue()/1000) + (opexIPD5- Double.parseDouble(RENOPD5.toString()).intValue()/1000) ))+"""</td>
    </tr>
"""
    result= result +
            """
	<tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;background:#f2f2f2;'>
      <td style='width: 30%;'><b>Variación NETA PD</b> </td>
	  <td style='width: 12%; text-align: right;font-weight: bold;'>0</td>
	  <td style='width: 12%;font-weight: bold; text-align: right;""" +( ((reiPD1+RENOPD1)/1000-opexIPD1).intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(((reiPD1+RENOPD1)/1000-opexIPD1).intValue())+"""</td>
	  <td style='width: 12%;font-weight: bold; text-align: right;""" +(((reiPD2+RENOPD2)/1000-opexIPD2).intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(((reiPD2+RENOPD2)/1000-opexIPD2).intValue())+"""</td>
	  <td style='width: 12%;font-weight: bold; text-align: right;""" +(((reiPD3+RENOPD3)/1000-opexIPD3).intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(((reiPD3+RENOPD3)/1000-opexIPD3).intValue())+"""</td>
	  <td style='width: 12%;font-weight: bold; text-align: right;""" +(((reiPD4+RENOPD4)/1000-opexIPD4).intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(((reiPD4+RENOPD4)/1000-opexIPD4).intValue())+"""</td>
	  <td style='width: 12%;font-weight: bold; text-align: right;""" +(((reiPD5+RENOPD5)/1000-opexIPD5).intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format(((reiPD5+RENOPD5)/1000-opexIPD5).intValue())+"""</td>
      <td style='width: 12%;font-weight: bold; text-align: right;""" +((((reiPD1+RENOPD1)/1000-opexIPD1)+((reiPD2+RENOPD2)/1000-opexIPD2)+((reiPD3+RENOPD3)/1000-opexIPD3)+((reiPD4+RENOPD4)/1000-opexIPD4)+((reiPD5+RENOPD5)/1000 -opexIPD5)  ).intValue() < 0?"color : red;" : "")+"""'>"""+NumberFormat.getIntegerInstance().format((  ((reiPD1+RENOPD1)/1000-opexIPD1)+((reiPD2+RENOPD2)/1000-opexIPD2)+((reiPD3+RENOPD3)/1000-opexIPD3)+((reiPD4+RENOPD4)/1000-opexIPD4)+((reiPD5+RENOPD5)/1000-opexIPD5)  ).intValue())+"""</td>
    </tr>
	</tbody>
	</table>
"""
}
else

{
    result= result +
            """
	<tr style='border-bottom: 1px solid #ccc; line-height: 1.4em;font-size: 13px;background:#f2f2f2;'>
      <td style='width: 30%;'><b>Variación NETA PD</b> </td>
	  <td style='width: 12%; text-align: right;font-weight: bold;'>0</td>
	  <td style='width: 12%; text-align: right;font-weight: bold;'>0</td>
	  <td style='width: 12%; text-align: right;font-weight: bold;'>0</td>
	  <td style='width: 12%; text-align: right;font-weight: bold;'>0</td>
	  <td style='width: 12%; text-align: right;font-weight: bold;'>0</td>
	  <td style='width: 12%; text-align: right;font-weight: bold;'>0</td>
      <td style='width: 12%; text-align: right;font-weight: bold;'>0</td>
    </tr>
	</tbody>
	</table>
"""
}

return result
def getEff(double opexim,double REI)
{
    if(REI == 0)
    {
        return "n.a."
    }else
    {
        return(  opexim / REI < 0 ? "n.a." :  NumberFormat.getIntegerInstance().format((opexim / REI ).round(2))   )
    }
}
def esCR() {     return issue.getCustomFieldValue(cf_categoria).getOptionId() == 10903 }
def esProyecto() //MFS
{
	return issue.getCustomFieldValue(cf_categoria).getOptionId() == 10900
}

def esProyectoComoServicio() //MFS
{
	return issue.getCustomFieldValue(cf_categoria).getOptionId() == 14003
}

