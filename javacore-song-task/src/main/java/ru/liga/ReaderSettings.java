package ru.liga;

import com.leff.midi.MidiFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ReaderSettings {

    static Logger log = LoggerFactory.getLogger(ReaderSettings.class);
    static void argsReader(String[] args) throws IOException {

        if (args[1].equals("analyze")&&args.length==2){
            Analysis a = new Analysis();
            try {
                a.getAnalyzeOfVoiceTrack(args[0]);
            }catch (FileNotFoundException ex){
                log.trace("Файл не найден");
            }
        }
        else if (args[1].equals("change")&& args.length==6&&args[2].equals("-trans")&&args[4].equals("-tempo")){
            ChangeFile cf = new ChangeFile();
            String path =args[0];
            int trans =Integer.parseInt(args[3]);
            float tempo =Float.parseFloat(args[5]);

            File file = new File(args[0]);
            String pathNew = getNewPath(trans,tempo,file);
            cf.getChangedMidi(path,trans,tempo).writeToFile(new File(pathNew));

        }
        else invalidRequest();

    }
    static void invalidRequest(){
        System.out.println("Неверный запрос");
        System.out.println("Попробуйте");
        System.out.println("java -jar midi-analyzer.jar \"C:\\zombie.mid\" analyze");
        System.out.println("Или");
        System.out.println("java -jar midi-analyzer.jar \"C:\\zombie.mid\" change -trans 2 -tempo 20");
    }
    public static MidiFile getMidiFile(String midiFilePath) throws IOException {
        return new MidiFile(new FileInputStream(midiFilePath));
    }
    static String getNewPath(int trans, float tempo, File file) {
        String newName = file.getName().replace(".mid", "") + "-trans" + trans + "-tempo" + tempo + ".mid";
        try {
            return file.getParentFile().getAbsolutePath() + File.separator + newName;
        } catch (NullPointerException ex) {
            log.trace("Путь к файлу введён некорректно");
            invalidRequest();
            return null;
        }
    }
}
