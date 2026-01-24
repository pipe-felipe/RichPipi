package domain

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import kotlin.test.assertContains
import kotlin.test.assertEquals

class BackupConstantsTest {

    @Test
    fun `getCurrentDateTime should return valid date format`() {
        val result = BackupConstants.getCurrentDateTime()

        // Verifica se não é nulo
        assertNotNull(result)

        // Verifica se o formato está correto: dd/MM/yyyy HH:mm
        val regex = Regex("""\d{2}/\d{2}/\d{4} \d{2}:\d{2}""")
        assertTrue(regex.matches(result), "Formato de data inválido: $result")

        // Verifica se contém as barras e dois pontos esperados
        assertContains(result, "/")
        assertContains(result, ":")
        assertContains(result, " ")
    }

    @Test
    fun `getCurrentDateTime should return current time approximately`() {
        val result = BackupConstants.getCurrentDateTime()

        // Verifica se o ano é razoável (entre 2024 e 2030)
        val year = result.split("/")[2].split(" ")[0].toInt()
        assertTrue(year >= 2024 && year <= 2030, "Ano fora do intervalo esperado: $year")
    }

    @Test
    fun `getBackupFolderWithTimestamp should contain default folder name`() {
        val result = BackupConstants.getBackupFolderWithTimestamp()

        assertNotNull(result)
        assertContains(result, BackupConstants.DEFAULT_FOLDER_NAME)
        assertContains(result, "_")

        // Verifica se não contém caracteres problemáticos para nomes de pastas
        assertTrue(
            !result.contains("/") && !result.contains(":") && !result.contains(" "),
            "Nome da pasta contém caracteres inválidos: $result"
        )
    }

    @Test
    fun `getBackupSpreadsheetWithTimestamp should contain default spreadsheet name`() {
        val result = BackupConstants.getBackupSpreadsheetWithTimestamp()

        assertNotNull(result)
        assertContains(result, BackupConstants.DEFAULT_SPREADSHEET_NAME)
        assertContains(result, "_")

        // Verifica se não contém caracteres problemáticos para nomes de arquivos
        assertTrue(
            !result.contains("/") && !result.contains(":") && !result.contains(" "),
            "Nome da planilha contém caracteres inválidos: $result"
        )
    }

    @Test
    fun `timestamp functions should generate consistent names`() {
        val folder1 = BackupConstants.getBackupFolderWithTimestamp()
        val spreadsheet1 = BackupConstants.getBackupSpreadsheetWithTimestamp()

        val folder2 = BackupConstants.getBackupFolderWithTimestamp()
        val spreadsheet2 = BackupConstants.getBackupSpreadsheetWithTimestamp()

        // Verifica se os nomes são válidos
        assertNotNull(folder1)
        assertNotNull(folder2)
        assertNotNull(spreadsheet1)
        assertNotNull(spreadsheet2)

        // Verifica se seguem o padrão esperado
        assertContains(folder1, BackupConstants.DEFAULT_FOLDER_NAME)
        assertContains(spreadsheet1, BackupConstants.DEFAULT_SPREADSHEET_NAME)
    }

    @Test
    fun `default constants should have expected values`() {
        assertEquals("rich-pipi-backup", BackupConstants.DEFAULT_FOLDER_NAME)
        assertEquals("rich-pipi-backup-sheet", BackupConstants.DEFAULT_SPREADSHEET_NAME)
    }

    @Test
    fun `timestamp format should be consistent between functions`() {
        val folderName = BackupConstants.getBackupFolderWithTimestamp()
        val spreadsheetName = BackupConstants.getBackupSpreadsheetWithTimestamp()

        // Extrai o timestamp dos nomes
        val folderTimestamp = folderName.removePrefix("${BackupConstants.DEFAULT_FOLDER_NAME}_")
        val spreadsheetTimestamp = spreadsheetName.removePrefix("${BackupConstants.DEFAULT_SPREADSHEET_NAME}_")

        // Os timestamps devem seguir o mesmo padrão: dd-MM-yyyy_HH-mm
        val timestampRegex = Regex("""\d{2}-\d{2}-\d{4}_\d{2}-\d{2}""")
        assertTrue(timestampRegex.matches(folderTimestamp), "Formato de timestamp da pasta inválido: $folderTimestamp")
        assertTrue(timestampRegex.matches(spreadsheetTimestamp), "Formato de timestamp da planilha inválido: $spreadsheetTimestamp")

        // Os timestamps devem ser iguais (chamados no mesmo minuto)
        assertEquals(folderTimestamp, spreadsheetTimestamp, "Timestamps das funções devem ser consistentes")
    }
}
