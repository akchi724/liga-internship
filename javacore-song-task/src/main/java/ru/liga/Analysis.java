package ru.liga;

import com.leff.midi.MidiFile;
import com.leff.midi.event.meta.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.util.SongUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.liga.App.eventsToNotes;
import static ru.liga.ReaderSettings.getMidiFile;

public class Analysis {
    Logger log = LoggerFactory.getLogger(Analysis.class);



    public boolean isVoice(List<Note> track) {
        log.trace("Проверка дорожки на пригодность для исполнения голосом");
        List<Note> vNotes = new ArrayList<>();
        Note exNote = null;
        if (track.size() == 0) return false;
        for (Note note : track) {
            if (exNote!=null) {
                if (rangesIntersect(exNote.startTick(), exNote.durationTicks(), note.startTick())) {
                    log.trace("Дорожка не пригодна для исполнения голосом");
                    return false;
                }
            }
            exNote=note;
        }
        log.trace("Дорожка пригодна для исполнения голосом");
        return true;
    }

    List<List<Note>> getAllTracksAsNotes(MidiFile midiFile) {
        log.trace("Получение всех дорожек");
        List<List<Note>> listNote = new ArrayList<>();
        for (int i = 0; i < midiFile.getTracks().size(); i++) {
            listNote.add(eventsToNotes(midiFile.getTracks().get(i).getEvents()));
        }
        return listNote;
    }

    List<List<Note>> getVoiceTracksAsNotes(MidiFile midiFile) {
        log.trace("Получение всех потенциальных голосовых дорожек Midi-файла");
        return getAllTracksAsNotes(midiFile).stream().filter(this::isVoice).collect(Collectors.toList());
    }

    boolean rangesIntersect(long firststart, long firstduration, long secondstart) {
        return secondstart < (firststart + firstduration) && secondstart > firststart;
    }

    public List<String> midiRange(List<Note> noteList) {
        log.info("Диапазон");
        List<String> mD = new ArrayList<>();
        Note upperNote = null;
        Note lowerNote = null;
        for (Note note : noteList) {
            if (upperNote == null) {
                upperNote = note;
                lowerNote = note;
            }
            if (note.getNote().getMidi() > upperNote.getNote().getMidi()) upperNote = note;
            if (note.getNote().getMidi() < lowerNote.getNote().getMidi()) lowerNote = note;
        }
        mD.add(upperNote.getNote().fullName());
        log.info("Верхняя:"+mD.get(0));
        mD.add(lowerNote.getNote().fullName());
        log.info("Нижняя:"+mD.get(1));
        mD.add("" + (upperNote.getNote().getMidi() - lowerNote.getNote().getMidi()));
        log.info("диапазон:"+mD.get(2));
        return mD;
    }

    HashMap<Integer, Integer> numOfNotesByDuration(List<Note> listNote, MidiFile midiFile) {
        log.info("Количество нот по длительности");
        HashMap<Integer, Integer> nONBD = new HashMap<>();
        for (Note note : listNote) {
            float bpm = SongUtils.getTempo(midiFile).getBpm();
            int ms = SongUtils.tickToMs(2, midiFile.getResolution(), note.durationTicks());
            if (nONBD.containsKey(ms)) {
                nONBD.put(ms, nONBD.get(ms) + 1);
            } else nONBD.put(ms, 1);
        }
        return nONBD;
    }

    HashMap<String, Integer> numOcurOfNotes(List<Note> listNote) {
        log.info("Список нот с количеством вхождений");
        HashMap<String, Integer> nONBD = new HashMap<>();
        for (Note note : listNote) {
            String noteName = note.getNote().fullName();
            if (nONBD.containsKey(noteName)) {
                nONBD.put(noteName, nONBD.get(noteName) + 1);
            } else nONBD.put(noteName, 1);
        }
        return nONBD;
    }
     List<Note> getVoiceTrack(MidiFile midiFile) {
         log.trace("Выявление голосовой дорожки");
        List<List<Note>> maybe = getVoiceTracksAsNotes(midiFile);
        long countOfTextEvents = getCountOfTextEvents(midiFile);
        List<Long> difference = maybe.stream().map((notes) -> {
            return Math.abs((long)notes.size() - countOfTextEvents);
        }).collect(Collectors.toList());
        long minDif = Collections.min(difference);
        return maybe.get(difference.indexOf(minDif));
     }
     void getAnalyzeOfVoiceTrack(String midiFilePath) throws IOException {
         log.info("Анализ голосовой дорожки:");
        MidiFile midiFile =getMidiFile(midiFilePath);
         List<Note> noteList=getVoiceTrack(midiFile);

        midiRange(getVoiceTrack(midiFile));
        for (Map.Entry entry:numOfNotesByDuration(noteList,midiFile).entrySet()){
            log.info(entry.getKey() +"мс: "+ entry.getValue());
        };
        for(Map.Entry entry:numOcurOfNotes(noteList).entrySet()){
            log.info(entry.getKey() +": "+ entry.getValue());
        }
     }
    long getCountOfTextEvents(MidiFile midiFile) {
        return midiFile.getTracks().stream().flatMap((midiTrack) -> {
            return midiTrack.getEvents().stream();
        }).filter((midiEvent) -> {
            return midiEvent.getClass().equals(Text.class);
        }).count();
    }
}
