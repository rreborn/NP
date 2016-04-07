/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverside;

/**
 *
 * @author Reborn
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import java.sql.*;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.version.LibVlcVersion;

public class PlayerTest {

        private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
        private final JFrame frame;
        private JButton pauseButton,skipButton,rewindButton;
        private TCPServer mServer;
        private Queue playList;
        private String dir="E:\\Film\\Kodoku no Gurume Season 4\\";
        private ResultSet rs;
        
        public static void main(final String[] args) {
            NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files\\VideoLAN\\VLC");
            Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new PlayerTest(args);
                }
              });
        }

        public PlayerTest(String[] args) {
            playList=new LinkedList();
            final Database db=new Database();
            
            frame = new JFrame("Media Player");
            frame.setBounds(100, 100, 600, 400);
          
            JPanel contentPane = new JPanel();
            contentPane.setLayout(new BorderLayout());

            mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
            contentPane.add(mediaPlayerComponent, BorderLayout.CENTER);

            JPanel controlsPane = new JPanel();
            pauseButton = new JButton("+");
            controlsPane.add(pauseButton);
            rewindButton = new JButton("-");
            controlsPane.add(rewindButton);
            skipButton = new JButton("Skip");
            controlsPane.add(skipButton);
            contentPane.add(controlsPane, BorderLayout.SOUTH);

            frame.setContentPane(contentPane);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            
            AddListener();
            
            frame.setVisible(true);
            
            try{
                rs=db.getLagu(1);
                playList.add("Eps - 01");
                while(rs.next()){
                mediaPlayerComponent.getMediaPlayer().playMedia(rs.getString("location_lagu"));
                }
            }catch(Exception e){
                
            }
            
            mServer = new TCPServer(new TCPServer.OnMessageReceived() {
                    @Override
                    //this method declared in the interface from TCPServer class is implemented here
                    //this method is actually a callback method, because it will run every time when it will be called from
                    //TCPServer class (at while)
                    public void messageReceived(String message) {
                        if(message.contains("Music"))
                        {
                            String val=message.split("Music ")[1];
                            playList.add(val);
                            System.out.println(val);
                            if(!mediaPlayerComponent.getMediaPlayer().isPlaying())
                            {
                                PlayMusic();
                            }
                        }
                        else if(message.contains("Play"))
                        {
                            mediaPlayerComponent.getMediaPlayer().play();
                        }
                        else if(message.contains("Pause"))
                        {
                            mediaPlayerComponent.getMediaPlayer().pause();
                        }
                        else if(message.contains("Stop"))
                        {
                            mediaPlayerComponent.getMediaPlayer().setTime(0);
                        }
                        else if(message.contains("Skip"))
                        {
                            playList.remove();
                            PlayMusic();
                        }
                        else if(message.contains("VMAS"))
                        {
                            int vol=mediaPlayerComponent.getMediaPlayer().getVolume();
                            if(message.contains("UP")){
                                vol+=5;
                                if(vol>100)
                                    vol=100;
                            }
                            else if(message.contains("DOWN")){
                                vol-=5;
                                if(vol<0)
                                    vol=0;
                            }
                            mediaPlayerComponent.getMediaPlayer().setVolume(vol);
                        }
                        
                        else if(message.contains("FINDS:")){
                            String namaLagu=message.split("FINDS:")[0];
                            ResultSet rs=db.getLagu(namaLagu);
                            String m="";
                            try{
                                while(rs.next()){
                                    int album=rs.getInt("id_album");
                                    int penyanyi=0;
                                    String nama=rs.getString("nama_lagu");
                                    String namaPenyanyi="";
                                    ResultSet rs1=db.getAlbum(album);
                                    while(rs1.next())
                                        penyanyi=rs1.getInt("id_penyanyi");
                                    rs1=db.getPenyanyi(penyanyi);
                                    while(rs1.next())
                                        namaPenyanyi=rs1.getString("nama_penyanyi");
                                    m=m+String.valueOf(rs.getInt("id_lagu"))+"-"+nama+"-"+namaPenyanyi+":";
                                }
                                m=m+"DONE";
                                mServer.sendMessage(m);
                            }catch(Exception e){
                                
                            }
                        }
                        
                        else if(message.contains("FINDA:")){
                            String namaLagu=message.split("FINDA:")[0];
                            ResultSet rs=db.getLagu(namaLagu);
                            String m="";
                            try{
                                while(rs.next()){
                                    int album=rs.getInt("id_album");
                                    int penyanyi=0;
                                    String nama=rs.getString("nama_lagu");
                                    String namaPenyanyi="";
                                    ResultSet rs1=db.getAlbum(album);
                                    while(rs1.next())
                                        penyanyi=rs1.getInt("id_penyanyi");
                                    rs1=db.getPenyanyi(penyanyi);
                                    while(rs1.next())
                                        namaPenyanyi=rs1.getString("nama_penyanyi");
                                    m=m+String.valueOf(rs.getInt("id_lagu"))+"-"+nama+"-"+namaPenyanyi+":";
                                }
                                m=m+"DONE";
                                mServer.sendMessage(m);
                            }catch(Exception e){
                                
                            }
                        }
                        
                        //else if(message.contains())
                        /*Belum Ada*/
                        else if(message.contains("Pitch")){
                            int vol=mediaPlayerComponent.getMediaPlayer().getVolume();
                            if(message.contains("+")){
                                vol++;
                                if(vol>100)
                                    vol=100;
                            }
                            else if(message.contains("-")){
                                vol--;
                                if(vol<0)
                                    vol=0;
                            }
                            mediaPlayerComponent.getMediaPlayer().setVolume(vol);
                        }
                    }
                });
                mServer.start();
        }
    private void AddListener()
    {
        frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    mediaPlayerComponent.release();
                    System.exit(0);
                }
            });
        /*pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                            int vol=mediaPlayerComponent.getMediaPlayer().getVolume();
                            
                            mediaPlayerComponent.getMediaPlayer().setVolume(vol+1);
            }
        });

        rewindButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                           int vol=mediaPlayerComponent.getMediaPlayer().getVolume();
                          
                            mediaPlayerComponent.getMediaPlayer().setVolume(vol-1);
            }
        });

        skipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.getMediaPlayer().stop();
            }
        });*/
        
        mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void stopped(MediaPlayer mediaPlayer){
                //playList.remove();
                //PlayMusic();
            }
            
            @Override
            public void playing(MediaPlayer mediaPlayer) {
                 SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setTitle(String.format(
                            "My First Media Player - %s",
                            mediaPlayerComponent.getMediaPlayer().getMediaMeta().getTitle()
                        ));
                    }
                });
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                  SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if(playList.isEmpty())
                            closeWindow();
                        else
                          {
                              playList.remove();
                              PlayMusic();
                          }
                    }
                });
            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(
                            frame,
                            "Failed to play media",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                        closeWindow();
                    }
                });
            }
        });
    }
    
    private void closeWindow() {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }
    
    private void PlayMusic()
    {
        String play=(String) playList.peek();
        System.out.println(play);
        System.out.println(dir+play+".mp4");
        mediaPlayerComponent.getMediaPlayer().playMedia(dir+play+".mp4");
    }
}