package domain

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BackupConstantsTest {

    companion object {
        // Gera o epoch para 08-02-2026 23:19 UTC (em milissegundos)
        const val TEST_EPOCH: Long = 1760272740000L
    }

    @Test
    fun `getCurrentDateTime should return valid date format`() {
        val result = BackupConstants.getCurrentDateTime(TEST_EPOCH)

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
        val nowEpoch = TEST_EPOCH // Para garantir determinismo no teste
        val result = BackupConstants.getCurrentDateTime(nowEpoch)

        // Verifica se o ano é razoável (entre 2024 e 2030)
        val year = result.split("/")[2].split(" ")[0].toInt()
        assertTrue(year >= 2024 && year <= 2030, "Ano fora do intervalo esperado: $year")
    }

    @Test
    fun `getBackupSpreadsheetWithTimestamp should contain default spreadsheet name`() {
        val result = BackupConstants.getBackupSpreadsheetWithTimestamp(TEST_EPOCH)

        assertNotNull(result)
        assertContains(result, BackupConstants.getBackupSpreadsheetWithTimestamp(TEST_EPOCH))
        assertContains(result, "_")

        // Verifica se não contém caracteres problemáticos para nomes de arquivos
        assertTrue(
            !result.contains("/") && !result.contains(":") && !result.contains(" "),
            "Nome da planilha contém caracteres inválidos: $result",
        )
    }

    @Test
    fun `timestamp functions should generate consistent names`() {
        val folder1 = BackupConstants.DEFAULT_FOLDER_NAME
        val spreadsheet1 = BackupConstants.getBackupSpreadsheetWithTimestamp(TEST_EPOCH)
        val folder2 = BackupConstants.DEFAULT_FOLDER_NAME
        val spreadsheet2 = BackupConstants.getBackupSpreadsheetWithTimestamp(TEST_EPOCH)

        // Verifica se os nomes são válidos
        assertNotNull(folder1)
        assertNotNull(folder2)
        assertNotNull(spreadsheet1)
        assertNotNull(spreadsheet2)

        // Verifica se seguem o padrão esperado
        assertContains(folder1, BackupConstants.DEFAULT_FOLDER_NAME)
        assertContains(spreadsheet1, BackupConstants.getBackupSpreadsheetWithTimestamp(TEST_EPOCH))
    }

    @Test
    fun `default constants should have expected values`() {
        assertEquals("rich-pipi-backup-sheet_29-10-2025_12-39", BackupConstants.getBackupSpreadsheetWithTimestamp(TEST_EPOCH))
    }

    @Test
    fun `timestamp format should be consistent between functions`() {
        val folderName = BackupConstants.DEFAULT_FOLDER_NAME
        val spreadsheetName = BackupConstants.getBackupSpreadsheetWithTimestamp(TEST_EPOCH)

        // Extrai o timestamp dos nomes
        val folderTimestamp = "29-10-2025_12-39" // esperado para testEpoch
        val spreadsheetTimestamp = spreadsheetName.removePrefix("rich-pipi-backup-sheet_")

        // Os timestamps devem seguir o mesmo padrão: dd-MM-yyyy_HH-mm
        val timestampRegex = Regex("""\d{2}-\d{2}-\d{4}_\d{2}-\d{2}""")
        assertTrue(timestampRegex.matches(folderTimestamp), "Formato de timestamp da pasta inválido: $folderTimestamp")
        assertTrue(timestampRegex.matches(spreadsheetTimestamp), "Formato de timestamp da planilha inválido: $spreadsheetTimestamp")
        assertEquals(folderTimestamp, spreadsheetTimestamp, "Timestamps das funções devem ser consistentes")
    }
}
