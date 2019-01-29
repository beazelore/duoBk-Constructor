package duobk_constructor.logic;

import java.util.Locale;

public class Language {
    public enum Lang {ENG, RUS, UKR, CATALAN,CZECH,DANISH,GERMAN,ESPERANTO,SPANISH,EST,FINNISH,FRENCH,HINDI,
        ARMENIAN,ITALIAN,LATIN,LITHUANIAN,LATVIAN,NO,DUTCH,PL,PT,RO,SLOVAK,SLOVENIAN,SWEDISH,TAMIL, TR, NONE}
    private Lang language;

    public Lang getLanguage() {
        return language;
    }
    @Override
    public String toString() {
        switch (language){
        case ENG:
            return  "en";
        case CATALAN:
            return  "ca";
        case CZECH:
            return  "cs";
        case DANISH:
            return  "da";
        case GERMAN:
            return  "de";
        case ESPERANTO:
            return  "eo";
        case SPANISH:
            return  "es";
        case EST:
            return  "et";
        case FINNISH:
            return  "fi";
        case FRENCH:
            return  "fr";
        case HINDI:
            return  "hi";
        case ARMENIAN:
            return  "hy";
        case ITALIAN:
            return  "it";
        case LATIN:
            return  "la";
        case LITHUANIAN:
            return  "lt";
        case LATVIAN:
            return  "lv";
        case NO:
            return  "no";
        case DUTCH:
            return  "nl";
        case PL:
            return  "pl";
        case PT:
            return  "pt";
        case RO:
            return  "ro";
        case RUS:
            return  "ru";
        case SLOVAK:
            return  "sk";
        case SLOVENIAN:
            return  "sl";
        case SWEDISH:
            return  "sv";
        case TR:
            return  "tr";
        case TAMIL:
            return  "ta";
        case UKR:
            return  "uk";
        default: return null;
    }
    }
    public Language(Lang language) {
        this.language = language;
    }
    public Language(String language){
        switch (language){
            case "ang":
            case "eng": default:
            case "enm":
            case "en":
                this.language = Lang.ENG;
                break;
            case "ca":
            case "cat":
                this.language = Lang.CATALAN;
                break;
            case "cs":
            case "cze":
            case "ces":
                this.language = Lang.CZECH;
                break;
            case "da":
            case "dan":
                this.language = Lang.DANISH;
                break;
            case "de":
            case "ger":
            case "deu":
            case "gmh":
            case "goh":
                this.language = Lang.GERMAN;
                break;
            case "eo":
            case "epo":
                this.language = Lang.ESPERANTO;
                break;
            case "es":
            case "spa":
                this.language = Lang.SPANISH;
                break;
            case "et":
            case "est":
                this.language = Lang.EST;
                break;
            case "fi":
            case "fin":
                this.language = Lang.FINNISH;
                break;
            case "fr":
            case "fre":
            case "fra":
            case "frm":
            case "fro":
            case "cpf":
                this.language = Lang.FRENCH;
                break;
            case "hi":
            case "hin":
                this.language = Lang.HINDI;
                break;
            case "hy":
            case "arm":
            case "hye":
                this.language = Lang.ARMENIAN;
                break;
            case "it":
            case "ita":
                this.language = Lang.ITALIAN;
                break;
            case "la":
            case "lat":
                this.language = Lang.LATIN;
                break;
            case "lt":
            case "lit":
                this.language = Lang.LITHUANIAN;
                break;
            case "lv":
            case "lav":
                this.language = Lang.LATVIAN;
                break;
            case "nn":
            case "nb":
            case "no":
                this.language = Lang.NO;
                break;
            case "nl":
            case "dut":
            case "nld":
                this.language = Lang.DUTCH;
                break;
            case "pl":
            case "pol":
                this.language = Lang.DUTCH;
                break;
            case "pt":
            case "por":
            case "cpp":
                this.language = Lang.PT;
                break;
            case "ro":
            case "rum":
            case "rom":
                this.language = Lang.RO;
                break;
            case "ru":
            case "rus":
                this.language = Lang.RUS;
                break;
            case "sk":
            case "slo":
            case "slk":
                this.language = Lang.SLOVAK;
                break;
            case "sl":
            case "slv":
                this.language = Lang.SLOVENIAN;
                break;
            case "sv":
            case "swe":
                this.language = Lang.SWEDISH;
                break;
            case "tr":
            case "tur":
            case "ota":
                this.language = Lang.TR;
                break;
            case "ta":
            case "tam":
                this.language = Lang.TAMIL;
                break;
            case "uk":
            case "ukr":
                this.language = Lang.UKR;
                break;
            /*default:
                throw  new Exception("Not correct language string identifier");*/
        }
    }
}

