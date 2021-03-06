/*== TIFF-Val ==================================================================================
The TIFF-Val v1.1.0 application is used for validate Tagged Image File Format (TIFF).
Copyright (C) 2013 Claire R�thlisberger (KOST-CECO)
-----------------------------------------------------------------------------------------------
TIFF-Val is a development of the KOST-CECO. All rights rest with the KOST-CECO. 
This application is free software: you can redistribute it and/or modify it under the 
terms of the GNU General Public License as published by the Free Software Foundation, 
either version 3 of the License, or (at your option) any later version. 
This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
See the follow GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; 
if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, 
Boston, MA 02110-1301 USA or see <http://www.gnu.org/licenses/>.
==============================================================================================*/

package ch.kostceco.tools.tiffval;

import java.io.File;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.kostceco.tools.tiffval.controller.Controller;
import ch.kostceco.tools.tiffval.logging.LogConfigurator;
import ch.kostceco.tools.tiffval.logging.Logger;
import ch.kostceco.tools.tiffval.logging.MessageConstants;
import ch.kostceco.tools.tiffval.service.ConfigurationService;
import ch.kostceco.tools.tiffval.service.TextResourceService;
import ch.kostceco.tools.tiffval.util.Util;

/**
 * Dies ist die Starter-Klasse, verantwortlich f�r das Initialisieren des
 * Controllers, des Loggings und das Parsen der Start-Parameter.
 * 
 * @author Rc Claire R�thlisberger, KOST-CECO
 */
public class TIFFVal implements MessageConstants
{

	private static final Logger		LOGGER	= new Logger( TIFFVal.class );

	private TextResourceService		textResourceService;
	private ConfigurationService	configurationService;

	public TextResourceService getTextResourceService()
	{
		return textResourceService;
	}

	public void setTextResourceService( TextResourceService textResourceService )
	{
		this.textResourceService = textResourceService;
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(
			ConfigurationService configurationService )
	{
		this.configurationService = configurationService;
	}

	/**
	 * Die Eingabe besteht aus Parameter 1: Pfad zum TIFF-File Parameter 2: Pfad
	 * zum Logging-Verzeichnis
	 * 
	 * @param args
	 */
	public static void main( String[] args )
	{

		ApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:config/applicationContext.xml" );

		// TODO: siehe Bemerkung im applicationContext-services.xml bez�glich
		// Injection in der Superklasse aller Impl-Klassen
		// ValidationModuleImpl validationModuleImpl = (ValidationModuleImpl)
		// context.getBean("validationmoduleimpl");

		TIFFVal TIFFVal = (TIFFVal) context.getBean( "TIFFVal" );

		// Ist die Anzahl Parameter (2) korrekt?
		if ( args.length < 2 ) {
			LOGGER.logInfo( TIFFVal.getTextResourceService().getText(
					ERROR_PARAMETER_USAGE ) );
			System.exit( 1 );
		}

		File tiffDatei = new File( args[0] );
		LOGGER.logInfo( TIFFVal.getTextResourceService().getText(
				MESSAGE_tiffvalIDATION, tiffDatei.getName() ) );

		// die Anwendung muss mindestens unter Java 6 laufen
		String javaRuntimeVersion = System.getProperty( "java.vm.version" );
		if ( javaRuntimeVersion.compareTo( "1.6.0" ) < 0 ) {
			LOGGER.logInfo( TIFFVal.getTextResourceService().getText(
					ERROR_WRONG_JRE ) );
			LOGGER.logInfo( TIFFVal.getTextResourceService().getText(
					MESSAGE_VALIDATION_INTERRUPTED ) );
			System.exit( 1 );
		}

		// Ueberpr�fung des 2. Parameters (Log-Verzeichnis)
		File directoryOfLogfile = new File( args[1] );
		if ( !directoryOfLogfile.exists() ) {
			directoryOfLogfile.mkdir();
		}

		// Im Logverzeichnis besteht kein Schreibrecht
		if ( !directoryOfLogfile.canWrite() ) {
			LOGGER.logInfo( TIFFVal.getTextResourceService().getText(
					ERROR_LOGDIRECTORY_NOTWRITABLE, directoryOfLogfile ) );
			LOGGER.logInfo( TIFFVal.getTextResourceService().getText(
					MESSAGE_VALIDATION_INTERRUPTED ) );
			System.exit( 1 );
		}

		if ( !directoryOfLogfile.isDirectory() ) {
			LOGGER.logInfo( TIFFVal.getTextResourceService().getText(
					ERROR_LOGDIRECTORY_NODIRECTORY ) );
			LOGGER.logInfo( TIFFVal.getTextResourceService().getText(
					MESSAGE_VALIDATION_INTERRUPTED ) );
			System.exit( 1 );
		}

		// Ueberpr�fung des 1. Parameters (TIFF-Datei): existiert die Datei?
		if ( !tiffDatei.exists() ) {
			LOGGER.logInfo( TIFFVal.getTextResourceService().getText(
					ERROR_TIFFFILE_FILENOTEXISTING ) );
			LOGGER.logInfo( TIFFVal.getTextResourceService().getText(
					MESSAGE_VALIDATION_INTERRUPTED ) );
			System.exit( 1 );
		}

		String originalTiffName = tiffDatei.getAbsolutePath();

		// Initialisierung Modul B (JHove-Validierung)
		// �berpr�fen der Konfiguration: existiert die JHoveApp.jar am
		// angebenen Ort?
		String jhoveApp = TIFFVal.getConfigurationService().getPathToJhoveJar();
		File fJhoveApp = new File( jhoveApp );
		if ( !fJhoveApp.exists()
				|| !fJhoveApp.getName().equals( "JhoveApp.jar" ) ) {

			LOGGER.logInfo( TIFFVal.getTextResourceService().getText(
					ERROR_JHOVEAPP_MISSING ) );
			System.exit( 1 );
		}

		// �berpr�fen der Konfiguration: existiert die jhove.conf am
		// angebenen Ort?
		String jhoveConf = TIFFVal.getConfigurationService()
				.getPathToJhoveConfiguration();
		File fJhoveConf = new File( jhoveConf );
		if ( !fJhoveConf.exists()
				|| !fJhoveConf.getName().equals( "jhove.conf" ) ) {

			LOGGER.logInfo( TIFFVal.getTextResourceService().getText(
					ERROR_JHOVECONF_MISSING ) );
			System.exit( 1 );
		}

		// Konfiguration des Loggings, ein File Logger wird zus�tzlich erstellt
		LogConfigurator logConfigurator = (LogConfigurator) context
				.getBean( "logconfigurator" );
		String logFileName = logConfigurator.configure(
				directoryOfLogfile.getAbsolutePath(), tiffDatei.getName() );

		LOGGER.logError( TIFFVal.getTextResourceService().getText(
				MESSAGE_tiffvalIDATION, tiffDatei.getName() ) );

		Controller controller = (Controller) context.getBean( "controller" );
		boolean okMandatory = controller.executeMandatory( tiffDatei,
				directoryOfLogfile );
		boolean ok = false;

		// die Validierungen A sind obligatorisch, wenn sie bestanden
		// wurden, k�nnen die restlichen
		// Validierungen, welche nicht zum Abbruch der Applikation f�hren,
		// ausgef�hrt werden.
		if ( okMandatory ) {
			ok = controller.executeOptional( tiffDatei, directoryOfLogfile );
		}

		ok = (ok && okMandatory);

		LOGGER.logInfo( "" );
		if ( ok ) {
			LOGGER.logInfo( TIFFVal.getTextResourceService().getText(
					MESSAGE_TOTAL_VALID, tiffDatei.getAbsolutePath() ) );
		} else {
			LOGGER.logInfo( TIFFVal.getTextResourceService().getText(
					MESSAGE_TOTAL_INVALID, tiffDatei.getAbsolutePath() ) );
		}
		LOGGER.logInfo( "" );

		// Ausgabe der Pfade zu den Jhove Reports, falls welche
		// generiert wurden
		if ( Util.getPathToReportJHove() != null ) {
			LOGGER.logInfo( TIFFVal.getTextResourceService().getText(
					MESSAGE_FOOTER_REPORTJHOVE, Util.getPathToReportJHove() ) );
		}

		LOGGER.logInfo( "" );
		LOGGER.logInfo( TIFFVal.getTextResourceService().getText(
				MESSAGE_FOOTER_TIFF, originalTiffName ) );
		LOGGER.logInfo( TIFFVal.getTextResourceService().getText(
				MESSAGE_FOOTER_LOG, logFileName ) );
		LOGGER.logInfo( "" );

		if ( okMandatory ) {
			LOGGER.logInfo( TIFFVal.getTextResourceService().getText(
					MESSAGE_VALIDATION_FINISHED ) );
		} else {
			LOGGER.logInfo( TIFFVal.getTextResourceService().getText(
					MESSAGE_VALIDATION_INTERRUPTED ) );
		}

		if ( ok ) {
			System.exit( 0 );
		} else {
			System.exit( 2 );
		}

	}

}
