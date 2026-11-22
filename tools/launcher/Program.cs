using System;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Windows.Forms;

namespace PS_Diagram_Launcher
{
    class Program
    {
        private const String PSDIAGRAM_FILE = "psdiagram.jar";
        private static String pathToUse = null;

         static void Main(string[] args)
         {
             AppDomain.CurrentDomain.UnhandledException += delegate(object sender, UnhandledExceptionEventArgs arguments)
             {
                 var exception = (Exception) arguments.ExceptionObject;
                 Console.WriteLine("Unhandled exception: " + exception);
                 MessageBox.Show("Při spuštění launcheru nastala chyba:\n" + exception, "Chyba", MessageBoxButtons.OK, MessageBoxIcon.Error);
                 Environment.Exit(2);
             };

             String cmdArguments = "";
             for (int i = 0; i < args.Length; i++) {
                 if (i > 0) {
                     cmdArguments += " ";
                 }
                 cmdArguments += "\"" + args[i] + "\"";
             }

             String currentDirectory = Directory.GetCurrentDirectory();
             String currentSelfDirectory = Path.GetDirectoryName(Assembly.GetEntryAssembly().Location);
             if (File.Exists(Path.Combine(currentDirectory, PSDIAGRAM_FILE))) {
                 pathToUse = currentDirectory;
             } else if (File.Exists(Path.Combine(currentSelfDirectory, PSDIAGRAM_FILE))) {
                 pathToUse = currentSelfDirectory;
             }

             if (pathToUse != null) {
                 String ownJavaw = Path.Combine(pathToUse, Path.Combine(Path.Combine("jre", "bin"), "javaw.exe"));
                 String localJavaw = Environment.GetEnvironmentVariable("JAVA_HOME");
                 if (localJavaw != null) {
                     localJavaw = Path.Combine(Path.Combine(localJavaw, "bin"), "javaw.exe");
                 }

                 if (File.Exists(ownJavaw)) {
                     // launch throuh own JRE
                     JavawPSD(ownJavaw, cmdArguments);
                 } else if (localJavaw != null && File.Exists(localJavaw)) {
                     // launch through local JRE
                     JavawPSD(localJavaw, cmdArguments);
                 } else {
                     // just try to launch it anyway
                     MessageBox.Show("Nepodařilo se lokalizovat Java Runtime Enviroment. PS Diagram pravděpodobně \nnebude možné spustit.", "Chyba: jre nebyl nenalezen", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                     Process.Start(Path.Combine(pathToUse, PSDIAGRAM_FILE), cmdArguments);
                 }
             } else {
                 String locations = currentDirectory;
                 if (!currentDirectory.ToLower().Equals(currentSelfDirectory.ToLower())) {
                     locations += "\n" + currentSelfDirectory;
                 }
                 MessageBox.Show("Potřebný soubor \"" + PSDIAGRAM_FILE + "\" nebyl nalezen.\n\nOčekávaná umístění:\n" + locations , "Chyba: soubor nenalezen", MessageBoxButtons.OK, MessageBoxIcon.Error);
                 Environment.Exit(1);
             }

             Environment.Exit(0);
         }

         private static void JavawPSD(String javaw, String args)
         {
             Process.Start(javaw, "-jar \"" + Path.Combine(pathToUse, PSDIAGRAM_FILE) + "\"" + " " + args);
         } 
    }
}
