/*== TIFF-Val ==================================================================================
The TIFF-Val application is used for validate Tagged Image File Format (TIFF).
Copyright (C) 2013 Claire Röthlisberger (KOST-CECO)
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

package ch.kostceco.tools.tiffval.validation.module2;

import java.io.File;

import ch.kostceco.tools.tiffval.exception.module2.ValidationBjhoveValidationException;
import ch.kostceco.tools.tiffval.validation.ValidationModule;

/**
 * Validierungsschritt 3c (einschaltbar) Formatvalidierung, mit JHOVE oder einer
 * ähnlichen Lösung sowie mit einem externem PDF/A-Validator (z.B. PDF/A Manager
 * von PDFTRON) aller nach Dateiformat ausgewählten Dateien in /content
 * (konfigurierbare Liste von den zu validierenden Dateiformaten).
 * Referenzierung in Log-Datei zu den allfälligen zusätzlich generierten
 * Logdateien der eingesetzten Programme.
 * 
 * @author Rc Claire Röthlisberger, KOST-CECO
 */

public interface ValidationBjhoveValidationModule extends ValidationModule
{

	public boolean validate( File tiffDatei, File directoryOfLogfile )
			throws ValidationBjhoveValidationException;

}
