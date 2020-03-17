package ru.liga;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import ru.liga.songtask.content.Content;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppTest {
    Analysis analysis;
    ReaderSettings readerSettings;
    @Before
    public void setup() throws IOException {
        analysis = new Analysis();
        readerSettings= new ReaderSettings();

    }

    @Test
    public void getAllTracksAsNotesTest() throws IOException {
        Assertions.assertThat(analysis.getAllTracksAsNotes(readerSettings.getMidiFile(Content.WRECKINGBALL)).size()).isEqualTo(13);
    }


    @Test
    public void isVoicesTest(){
        List<Note> noteList= new ArrayList<>();
        noteList.add(new Note(NoteSign.E_1, 10, 10));
        noteList.add(new Note(NoteSign.E_1, 11, 10));
        noteList.add(new Note(NoteSign.E_1, 22, 10));
        noteList.add(new Note(NoteSign.E_1, 33, 10));
        Assertions.assertThat(analysis.isVoice(noteList)).isEqualTo(false);
        noteList= new ArrayList<>();
        noteList.add(new Note(NoteSign.E_1, 10, 10));
        noteList.add(new Note(NoteSign.E_1, 21, 10));
        noteList.add(new Note(NoteSign.E_1, 32, 10));
        noteList.add(new Note(NoteSign.E_1, 43, 10));
        Assertions.assertThat(analysis.isVoice(noteList)).isEqualTo(true);
    }
@Test
    public void isVoicesMyTest(){
        List<Note> noteList= new ArrayList<>();
        noteList.add(new Note(NoteSign.E_1, 10, 10));
        noteList.add(new Note(NoteSign.E_1, 11, 10));
        noteList.add(new Note(NoteSign.E_1, 22, 10));
        noteList.add(new Note(NoteSign.E_1, 33, 10));
        Assertions.assertThat(analysis.isVoice(noteList)).isEqualTo(false);
        noteList= new ArrayList<>();
        noteList.add(new Note(NoteSign.E_1, 10, 10));
        noteList.add(new Note(NoteSign.E_1, 21, 10));
        noteList.add(new Note(NoteSign.E_1, 32, 10));
        noteList.add(new Note(NoteSign.E_1, 43, 10));
        Assertions.assertThat(analysis.isVoice(noteList)).isEqualTo(true);
    }

    @Test
    public void rangesIntersectTest() {
        Assertions.assertThat(analysis.rangesIntersect(10, 10, 12)).isEqualTo(true);

    }

    @Test
    public void midiRangeTest() throws IOException {
        List<Note> listNotes = new ArrayList<>();

        listNotes.add(new Note((NoteSign.E_1), 10, 10));
        listNotes.add(new Note((NoteSign.F_2), 10, 10));
        listNotes.add(new Note((NoteSign.E_2), 10, 10));

        Assertions.assertThat(analysis.midiRange(listNotes).get(0)).isEqualTo("F2");
        Assertions.assertThat(analysis.midiRange(listNotes).get(1)).isEqualTo("E1");
        Assertions.assertThat(analysis.midiRange(listNotes).get(2)).isEqualTo("13");
    }

    //    @Test
//    public void getVoiceTracksAsNotesTest() throws IOException {
//        System.out.println(analysis.isVoice().size());
//        System.out.println(analysis.getAllListNotes().size());
//
//    }
    @Test
    public void numOfNotesByDurationTest() throws IOException {
        List<Note> noteList = new ArrayList<>();
        noteList.add(new Note(NoteSign.E_1, 10, 10));
        noteList.add(new Note(NoteSign.F_2, 10, 10));
        noteList.add(new Note(NoteSign.E_4, 10, 12));
        noteList.add(new Note(NoteSign.G_3, 10, 9));

        Assertions.assertThat(analysis.numOfNotesByDuration(noteList, readerSettings.getMidiFile(Content.WRECKINGBALL)).size()).isEqualTo(3);
        Assertions.assertThat(analysis.numOfNotesByDuration(noteList, readerSettings.getMidiFile(Content.WRECKINGBALL)).get(625)).isEqualTo(2);
        Assertions.assertThat(analysis.numOfNotesByDuration(noteList, readerSettings.getMidiFile(Content.WRECKINGBALL)).get(562)).isEqualTo(1);
        Assertions.assertThat(analysis.numOfNotesByDuration(noteList, readerSettings.getMidiFile(Content.WRECKINGBALL)).get(750)).isEqualTo(1);

    }

    @Test
    public void numOcurOfNotesTest() {
        List<Note> noteList = new ArrayList<>();
        noteList.add(new Note(NoteSign.E_1, 10, 10));
        noteList.add(new Note(NoteSign.E_1, 10, 10));
        noteList.add(new Note(NoteSign.E_4, 10, 12));
        noteList.add(new Note(NoteSign.G_3, 10, 9));

        Assertions.assertThat(analysis.numOcurOfNotes(noteList).get("E1")).isEqualTo(2);
        Assertions.assertThat(analysis.numOcurOfNotes(noteList).get("E4")).isEqualTo(1);
        Assertions.assertThat(analysis.numOcurOfNotes(noteList).get("G3")).isEqualTo(1);

    }

}
