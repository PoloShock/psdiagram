/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app;

import cz.miroslavbartyzal.psdiagram.app.gui.MainWindow;
import javax.swing.ToolTipManager;

/**
 * Hlavní třída obsahuje main metodu, sloužící pro spuštění hlavního
 * okna aplikace.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public final class Main
{

    // TODO az budu dynamicky menit layout, budu muset prepsat odkazy na layout ve vsech tridach jako by reference: AtomicReference<Object> ref = new AtomicReference<Object>("Hello");
    // TODO je-li v else vetvi jen podminka, je to pri exportu elseif!
    // TODO mozna pridat komentar do XML a exportu PDF, kodu.. o PS Diagramu
    // TODO do budoucna podprogram moznst expandovat/collapsovat :)
    // TODO nastaveni zarovnavani komentarovych textu
    // TODO odkazy na weby obrazku
    //e TODO exception handling + updater: http://stackoverflow.com/questions/4590295/catch-exception-high-in-the-call-stack-when-dealing-with-n-tiers
    //e TODO logovani + updater
    // TODO pridat info o verzi a licenci
    // TODO prepnuti focusu z platna do textboxu i pri editaci funkce symbolu
    // TODO ukolovani studentu? (seradit spravne kroky algoritmu, vyuziti jen urciteho poctu prikazu apod.)
    // TODO možnost nastavení barev v editacnim i animacnim rezimu
    // TODO po dvojitem poklepani na symbol, prepnout docasne na zalozku Text (nebo toggle?) Docasne proto, aby po vlozeni dalsiho symbolu jiz byla opet vybrana Funkce
    // TODO pro skoly nechat defaultni nastaveni jako readonly soubor xml primo u aplikace (co ale s prekopirovanim domu?) - NEBO co preferovat allusers slozku? :) + myslenka: u aplikace zkopirovat soubor do usera pouze pokud uz tam neni vlastni nastaveni
    // TODO menit i zeditovany text symbolu, pokud je zeditovany pouze o mezery ci odradkovani (nebo tak vymyslet automaticke odradkovavani?)
    // TODO moznost nastaveni prirazovaciho znamenka
    // TODO moznost prepnuti mezi Do-Until a Do-While
    // TODO doplnit celociselny podil (DIV v pascalu) -> co treba "//" jako v pythonu?
    // TODO vytvor si testy na syntakticke filtry
    // TODO doplnit priklad celociselneho deleni do symbolu procesu
    // TODO pomoci tranc preklopit TBLR layout na LFTB? :)
    // TODO mail Havelkova 19.4.
    // TODO misto blokovani syntaktickym filtrem, pouze zcervenit
    // TODO umoznit editaci vytvorenych promennych? (za behu animace)
    // TODO uvazit prebarveni pozadi animace - kouknout na to s modrym odstinem z prezentace diplomek
    // TODO vedle exportu do obrázku a PDF, vytvořit export do HTML! :)
    // TODO dat uzivateli vedet ze pri exportu do obrazku zalezi na aktualnim zvetseni! (nebo to udelat nejak jinak)
    // TODO pridat input symbolu text, aby nebylo nutne pouzivat output symbol pro oznameni co promenna znamena..
    // TODO animace uprav layoutu
    // TODO moznost promitani vytvořených algoritmů na webu (pro pasivní učení a prezentaci)? - Jelinek
    // TODO merit zakovi cas za ktery ulohu vykona, monitorovat problemy, se kterymi se setkal (co treba zachovavat vsechny stavy diagramu?) - Jelinek
    // TODO Timer, udalosti jako procedura (interrupt či vlákno)
    // TODO mrkni na http://jelastic.com/ a na jejich barvy, maji to cool :); http://www.noip.com/, http://www.hwinfo.com/, http://dinopoloclub.com/minimetro/, http://msysgit.github.io/
    // 			- co takhle jako metro mit nejakou kulickovou linku, v uvodu mit edit mode barvy s pobidkou jit dolu -> preslo by se do debug/anim modu a kulicka by sledovala pozici scrollu, ukotvila by se v jednotlivych vertikalnich sekcich (a treba tam tak nejak 2D kmitala)
    //			- mohlo by se za ni chytit jako easter egg a samozrejme prvky na strance budou opet hazet stiny
    // TODO zkontrolovat obfuskaci po upgradu proguardu
    // TODO check these: http://sourceforge.net/projects/flowcharts/?source=recommended, http://sourceforge.net/projects/simpeflowd/?source=recommended, http://sourceforge.net/projects/devflowcharter/?source=recommended, http://sourceforge.net/projects/javablock/
    // TODO sjednotit jazyk na EN (dokumentace)
    // TODO inspiruj se, koukni na Scratch
    // TODO pamatovat si velikost oken (http://stackoverflow.com/questions/7777640/best-practice-for-setting-jframe-locations)
    // TODO otestovat kdyz chybi consolas
    // TODO Pascal podpora vice vstupu naraz - prikaz read[ln](x,y,z)
    // TODO co to rucni propojovani sipek, kdyz bych je oznacil jinou barvou jako neoptimalni?
    // TODO moznost zkompilovat diagram do spustitelneho souboru
    // TODO zvazit pouziti delty (viz opengl projekt) pri animaci
    // TODO nemel bych pro ziskani casu posilat HTTP HEAD misto GET?
    // TODO drag&drop nejen do PSD ale i z nej - presun diagramu jako export tahem do emailove prilohy apod.
    // TODO u proguardu dynamicky do manifestu priradit main podle jeho obfuskace - tak nebudu muset zachovavat balickovou cestu k mainu
    // TODO pohyb mezi symboly v editacnim rezimu pomoci kurzorovych sipek
    // TODO podivat se na logicnost posouvani diagramu pri posunu komentare mimo platno a zpet
    // TODO pridat info o poctu vykonanych prikazu po konceni algoritmu -> hezky benchmark, kriterium pro hodnoceni efektivity algoritmu :)
    // TODO mozny nazev: dialgo
    // TODO "Soubor PS_Diagram_1.3.zip se běžně nestahuje a mohl by být nebezpečný."
    //e TODO pridat dalsi watermark napovedy
    // TODO v polich pro nazev promenne nabizet jiz existujici promenne
    //e TODO break by mel fungovat i pri animaci..?
    // TODO Do ulozky integrovat historii vytvoreni diagramu pomoci systemtime timestampu. Undo akci do teto historie zahrnovat jako beznou akci editace. Pri nacteni diagramu by se meli nacist i undoable edits, pozor ale na ty undo akce viz predchozi veta. Rozhranni pro timetravel (historie) muze pockat.
    //      - budu pak muset upravit i podminky pro ukladani a prompty pro ulozeni diagramu - diagram s jedinymi symboly Start-End jiz stoji za ukladani pokud ma historii...
    //      - taky asi bude potreba snizit interval ukladani zalohy kvuli objemnejsimu savu?
    // TODO vyresit ty netransparentni stiny v pdf
    // TODO predelat nalezani otevrenych PSDcek v updateru tak, aby se hledalo podle umisteni souboru a ne podle titulku jeho okna (POZOR: co kdyz to bude bezet z jineho JVMka, bude porad cesta k psd.jar nejak vyhledatelna?)
    // TODO co se deje pri pole = [1, 2, 4][8, 9, 8]??
    //e!! TODO otestovat windows spousteni pres cmd s cestou obsahujici mezeru
    // TODO priblizovani k mysi porad k pravemu dolnimu rohu (max scrollbarech) blbne (preskakuje) -> co to vyresit tak, ze prepisu chovani toho scrollview aby pri enablovani scrollbaru neubiral velikost canvasu, ale jen prekryl kontent?! :))
    // TODO resolvnout ten problem se stazenim (http://stackoverflow.com/questions/9512919/getting-around-chromes-malicious-file-warning)
    // TODO umoznit kompilaci s ruznymi priznaky at vim, jestli je PSD stazene z ulozto nebo odjinud
    // TODO isnpirace k helpu: http://www.bfoit.org/itp/JavaOperators.html
    // inspirace o vytvareni IDE: http://www.ibm.com/developerworks/opensource/tutorials/os-ecl-commplgin1/index.html
    //e TODO naseptavac JS funkci
    //e! TODO proverit chybu pri updatovani, viz email Jan Listopad
    //e TODO zbavit se tlacitka prerusit pri I/O symbolu tam, kde neni relevantni (krokovani -> zajistit nejak pristup k animaci kde je stav automatickeho prochazeni v booleanu?)
    // TODO nahradit Graphics2D g2 = (Graphics2D) g.create();, protoze g se reusuje a nemuzu ho menit...
    // TODO hezky algoritmus by byl treba odstraneni duplikatu v poli
    // TODO stejne jako plotly se po nejake dobe zeptat uzivatele co zlepsit a co se mu libilo (viz muj gmail)
    // TODO aktualizovat subprocess handling podle clanku: http://zeroturnaround.com/rebellabs/how-to-deal-with-subprocesses-in-java/
    // TODO krasne javadoc tipy: http://zeroturnaround.com/rebellabs/reasons-tips-and-tricks-for-better-java-documentation/
    // TODO zahrnout do parsovani i promenne a jejich typy na zaklade operaci s nimi - tim se zbavit nekonzistence v zakazu ciselne hodnoty jako logicke, kdyz to pritom javascript dovoluje - if(1)
    // TODO implementovat escapovani \n\r\t... (\" a \' uz mam implementovane) uvnitr stringu do/z pascalu
    //      - http://www.textfiles.com/bitsavers/pdf/borland/TURBO_Pascal_Reference_Manual_CPM_Version_3_Dec88.pdf
    //      - ftp://ftp.freepascal.org/fpc/docs-pdf/ref.pdf
    // TODO pri otevreni napr. nejvetsiho spol. del. v maximalizovanem okne a naslednem zmenseni okna, scrollbary indikuji vytsi diagram nez je (prestoze je zobrazen cely). Po pohnuti s platnem je to ok.
    //! TODO pri novem diagramu se neresetuje jeho poloha takze to zustane nascrolovane mimo obraz + crollbary se neaktualizuji
    /**
     * Metoda pro spuštění hlavního okna aplikace. Nejsou přijímány žádné
     * parametry.
     * <p>
     * @param args
     */
    public static void main(String[] args)
    {
        ToolTipManager.sharedInstance().setDismissDelay(12000); // nastavení tooltipů tak, aby zůstali 12 sekund
        MainWindow.main(args);
    }

}
