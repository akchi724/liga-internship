package ru.liga;


import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static ru.liga.ReaderSettings.getMidiFile;

public class ChangeFile {
    Logger log = LoggerFactory.getLogger(ChangeFile.class);

    public MidiFile getChangedMidi(String midiPath, int trans, float tempo) throws IOException {
        log.trace("Изменение скорости midi-Файла на " + tempo + "% и изменение тона на " + trans);
        MidiFile midiFile = getMidiFile(midiPath);
        float percentTempo = 1.0F + tempo / 100.0F;
        MidiFile modifiedMidiFile = changeTempoOfMidiFile(midiFile, percentTempo);
        modifiedMidiFile = transposeMidi(modifiedMidiFile, trans);
        return modifiedMidiFile;
    }

    private MidiFile transposeMidi(MidiFile midiFile, int trans) {
        log.trace("Изменение тона");
        MidiFile midiFile1 = new MidiFile();
        for (MidiTrack midiTrack : midiFile.getTracks()) {
            midiFile1.addTrack(transposeMidiTrack(trans, midiTrack));
        }
        return midiFile1;
    }

    private static MidiTrack transposeMidiTrack(int trans, MidiTrack midiTrack) {
        MidiTrack midiTrack1 = new MidiTrack();
        for (MidiEvent event : midiTrack.getEvents()) {
            if (event.getClass().equals(NoteOn.class)) {
                NoteOn on = getChangedNoteOn(trans, (NoteOn) event);
                midiTrack1.getEvents().add(on);
            } else if (event.getClass().equals(NoteOff.class)) {
                NoteOff off = getChangedNoteOff(trans, (NoteOff) event);
                midiTrack1.getEvents().add(off);
            } else {
                midiTrack1.getEvents().add(event);
            }
        }
        return midiTrack1;
    }

    private static NoteOn getChangedNoteOn(int trans, NoteOn midiEvent) {
        NoteOn on = new NoteOn(midiEvent.getTick(), midiEvent.getDelta(), midiEvent.getChannel(), midiEvent.getNoteValue(), midiEvent.getVelocity());
        if (on.getNoteValue() + trans <= 107 && on.getNoteValue() + trans >= 21) {
            on.setNoteValue(on.getNoteValue() + trans);
            return on;
        } else {
            throw new RuntimeException();
        }
    }

    private static NoteOff getChangedNoteOff(int trans, NoteOff midiEvent) {
        NoteOff off = new NoteOff(midiEvent.getTick(), midiEvent.getDelta(), midiEvent.getChannel(), midiEvent.getNoteValue(), midiEvent.getVelocity());
        if (off.getNoteValue() + trans <= 107 && off.getNoteValue() + trans >= 21) {
            off.setNoteValue(off.getNoteValue() + trans);
            return off;
        } else {
            throw new RuntimeException();
        }
    }

    public MidiFile changeTempoOfMidiFile(MidiFile midiFile, float percentTempo) {
        log.trace("Изменение скорости");
        MidiFile mf = new MidiFile();
        for (MidiTrack track : midiFile.getTracks()) {
            mf.addTrack(changeTempoOfMidiTrack(percentTempo, track));
        }
        return mf;
    }

    private static MidiTrack changeTempoOfMidiTrack(float percentTempo, MidiTrack midiTrack) {
        MidiTrack midiTrack1 = new MidiTrack();
        for (MidiEvent event : midiTrack.getEvents()) {
            if (event.getClass().equals(Tempo.class)) {
                Tempo tempo = getChangedTempo(percentTempo, (Tempo) event);
                midiTrack1.getEvents().add(tempo);
            } else
                midiTrack1.getEvents().add(event);
        }
        return midiTrack1;
    }

    private static Tempo getChangedTempo(float percentTempo, Tempo midiEvent) {
        Tempo tempo = new Tempo(midiEvent.getTick(), midiEvent.getDelta(), midiEvent.getMpqn());
        tempo.setBpm(tempo.getBpm() * percentTempo);
        return tempo;
    }


}
