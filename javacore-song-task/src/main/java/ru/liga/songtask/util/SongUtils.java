package ru.liga.songtask.util;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.meta.Tempo;
import ru.liga.songtask.domain.Note;

import java.util.ArrayList;
import java.util.List;

import static ru.liga.App.eventsToNotes;

public class SongUtils {

    /**
     * Перевод тиков в миллисекунды
     * @param bpm - количество ударов в минуту (темп)
     * @param resolution - midiFile.getResolution()
     * @param amountOfTick - то что переводим в миллисекунды
     * @return
     */
    public static int tickToMs(float bpm, int resolution, long amountOfTick) {
        return (int) (((60 * 1000) / (bpm * resolution)) * amountOfTick);
    }

    public static List<List<Note>> getAllTracksAsNoteLists(MidiFile midiFile) {
//        logger.trace("Процедура извлечерия треков из файла в виде List<Notes>");
        List<List<Note>> allTracks = new ArrayList();

        for(int i = 0; i < midiFile.getTracks().size(); ++i) {
            List<Note> tmp = eventsToNotes(((MidiTrack)midiFile.getTracks().get(i)).getEvents());
            if (tmp.size() > 0) {
                allTracks.add(tmp);
            }
        }

//        logger.trace("Извлечены все треки {} из файла.", allTracks.size());
        return allTracks;
    }
    public static Tempo getTempo(MidiFile midiFile) {
        Tempo tempo = (Tempo)((MidiTrack)midiFile.getTracks().get(0)).getEvents().stream().filter((value) -> value instanceof Tempo).findFirst().get();
        return tempo;
    }
}
