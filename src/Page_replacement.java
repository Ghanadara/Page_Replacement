import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.*;

public class Page_replacement {
    public static void main(String[] args)  {
        int m = Integer.parseInt(JOptionPane.showInputDialog("Menu\n[1]FIFO\n[2]Optimal\n[3]LRU\n[4]Second-Chance\n[5]LFU\n[6]Second_Chance_with_LRU\n\nEnter Choice:"));

        if (m == 1) {
            FIFO();
        } else if (m == 2) {
            Optimal();
        } else if (m == 3) {
            LRU();
        } else if (m == 4) {
            Second_Chance();
        } else if (m == 5) {
            LFU();
        } else if (m == 6) {
            Second_Chance_with_LRU();
        } else {
            JOptionPane.showMessageDialog(null,"Error");
        }
    }

    public static void FIFO() {
        Dimension dim = new Dimension(600, 400);
        JFrame frame = new JFrame("Page table");
        frame.setLocation(300, 400);
        frame.setPreferredSize(dim);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        int frames, pointer = 0, hit = 0, fault = 0;
        String[] buffer;//현재 프레임에 들어갈 정보 저장
        String[] reference;//reference string
        String[][] memorytable;//frame

        frames = Integer.parseInt(JOptionPane.showInputDialog("프레임의 수를 입력하세요"));

        String refString = JOptionPane.showInputDialog("Enter Reference String:");
        reference = refString.split("");//한글자씩 끊어서 reference에 저장

        memorytable = new String[frames][reference.length];//프레임
        buffer = new String[frames];
        for (int j = 0; j < frames; j++)//초기 버퍼값 -1로 설정
            buffer[j] = String.valueOf(-1);

        for (int i = 0; i < reference.length; i++) {//reference string의 길이만큼 반복
            int search = -1;
            for (int j = 0; j < frames; j++) {//hit난 경우
                if (Objects.equals(buffer[j], reference[i])) {
                    search = j;
                    hit++;
                    break;
                }
            }
            if (search == -1) {//hit나지 않은 경우
                buffer[pointer] = reference[i];//가장 오래된 값이 0번에 가깝게 있으므로 교체한다.
                fault++;//fault 증가
                pointer++;//다음에 교체될 버퍼의 인덱스값 증가
                if (pointer == frames)//버퍼의 인덱스값이 프레임 개수와 같은 경우
                    pointer = 0;//0으로 초기화
            }
            for (int j = 0; j < frames; j++)//버퍼의 내용 덮어씌우기
                memorytable[j][i] = buffer[j];
        }

        JTable table = new JTable(memorytable, reference);//표로 표현하기 위한 JTable 객체 생성
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < reference.length; i++) {//셀 너비 설정
            table.getColumnModel().getColumn(i).setPreferredWidth(20);
        }
        JScrollPane scrollPane = new JScrollPane(table);//표를 스크롤로 설정
        scrollPane.setPreferredSize(new Dimension(300, 200));
        // DefaultTableCellHeaderRenderer 생성 (가운데 정렬을 위한)
        DefaultTableCellRenderer tScheduleCellRenderer = new DefaultTableCellRenderer();
        // DefaultTableCellHeaderRenderer의 정렬을 가운데 정렬로 지정
        tScheduleCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        // 정렬할 테이블의 ColumnModel을 가져옴
        TableColumnModel tcmSchedule = table.getColumnModel();
        // 반복문을 이용하여 테이블을 가운데 정렬로 지정
        for (int i = 0; i < tcmSchedule.getColumnCount(); i++) {
            tcmSchedule.getColumn(i).setCellRenderer(tScheduleCellRenderer);
        }

        JTextArea Hit = new JTextArea("Hit의 수 : " + (hit));
        JTextArea Hitratio = new JTextArea("히트율 :" + (float) ((float) hit / reference.length) * 100 + "%");
        JTextArea Page_Fault = new JTextArea("Page Fault의 수 : " + fault);
        Hit.setLocation(100, 100);
        Hitratio.setLocation(100, 200);
        Page_Fault.setLocation(100, 300);
        JPanel panel = new JPanel();
        panel.add(Hit);
        panel.add(Hitratio);
        panel.add(Page_Fault);

        JButton backMain = new JButton("돌아가기");//main으로 돌아가기
        backMain.addActionListener(e -> {
            frame.dispose();
            main(null);
        });

        frame.add(scrollPane, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.WEST);
        frame.add(backMain,BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    public static void Optimal() {
        Dimension dim = new Dimension(600, 400);
        JFrame frame = new JFrame("Page table");
        frame.setLocation(300, 400);
        frame.setPreferredSize(dim);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        int frames, pointer = 0, hit = 0, fault = 0;
        boolean isFull = false;
        String[] buffer;//현재 프레임에 들어갈 정보 저장
        String[] reference;//reference string
        String[][] memorytable;//frame


        frames = Integer.parseInt(JOptionPane.showInputDialog("프레임의 수를 입력하세요"));
        String refString = JOptionPane.showInputDialog("Enter Reference String:");
        reference = refString.split("");//한글자씩 끊어서 reference에 저장
        memorytable = new String[frames][reference.length];//프레임
        buffer = new String[frames];
        for (int j = 0; j < frames; j++)//초기 버퍼값 -1로 설정
            buffer[j] = String.valueOf(-1);

        for (int i = 0; i < reference.length; i++) {//reference string의 길이만큼 반복
            int search = -1;
            for (int j = 0; j < frames; j++) {//hit난 경우
                if (Objects.equals(buffer[j], reference[i])) {
                    search = j;
                    hit++;
                    break;
                }
            }
            if (search == -1) {
                if (isFull) {//프레임이 가득찬 경우
                    int[] index = new int[frames];
                    boolean[] index_flag = new boolean[frames];
                    for (int j = i + 1; j < reference.length; j++) {//다음번에 제일 마지막까지
                        for (int k = 0; k < frames; k++) {
                            if (Objects.equals(reference[j], buffer[k]) && (!index_flag[k])) {
                                index[k] = j;//k번째 버퍼의값이 다음에 다시 나오는 j값을 저장
                                index_flag[k] = true;//찾았다고 설정
                                break;
                            }
                        }
                    }
                    int max = index[0];
                    pointer = 0;
                    if (max == 0) max = Integer.MAX_VALUE;
                    for (int j = 0; j < frames; j++) {//index배열의 값 중 max값을 찾는다.
                        if (index[j] == 0) index[j] = Integer.MAX_VALUE;
                        if (index[j] > max) {
                            max = index[j];
                            pointer = j;
                        }
                    }
                }
                buffer[pointer] = reference[i];//버퍼 수정
                fault++;
                if (!isFull) {//프레임에 들어갈 공간이 있는 경우
                    pointer++;//버퍼 인덱스값 증가
                    if (pointer == frames) {//버퍼의 인덱스값이 프레임 개수와 같은 경우
                        pointer = 0;//0으로 초기화
                        isFull = true;//가득찼다고 설정
                    }
                }
            }
            for (int j = 0; j < frames; j++)//버퍼의 내용 덮어씌우기
                memorytable[j][i] = buffer[j];
        }

        JTable table = new JTable(memorytable, reference);//표로 표현하기 위한 JTable 객체 생성
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < reference.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(20);
        }
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        // DefaultTableCellHeaderRenderer 생성 (가운데 정렬을 위한)
        DefaultTableCellRenderer tScheduleCellRenderer = new DefaultTableCellRenderer();
        // DefaultTableCellHeaderRenderer의 정렬을 가운데 정렬로 지정
        tScheduleCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        // 정렬할 테이블의 ColumnModel을 가져옴
        TableColumnModel tcmSchedule = table.getColumnModel();
        // 반복문을 이용하여 테이블을 가운데 정렬로 지정
        for (int i = 0; i < tcmSchedule.getColumnCount(); i++) {
            tcmSchedule.getColumn(i).setCellRenderer(tScheduleCellRenderer);
        }

        JTextArea Hit = new JTextArea("Hit의 수 : " + (hit));
        JTextArea Hitratio = new JTextArea("히트율 :" + (float) ((float) hit / reference.length) * 100 + "%");
        JTextArea Page_Fault = new JTextArea("Page Fault의 수 : " + fault);
        Hit.setLocation(100, 100);
        Hitratio.setLocation(100, 200);
        Page_Fault.setLocation(100, 300);
        JPanel panel = new JPanel();
        panel.add(Hit);
        panel.add(Hitratio);
        panel.add(Page_Fault);

        JButton backMain = new JButton("돌아가기");//main으로 돌아가기
        backMain.addActionListener(e -> {
            frame.dispose();
            main(null);
        });
        frame.add(scrollPane, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.WEST);
        frame.add(backMain,BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    public static void LRU()  {
        Dimension dim = new Dimension(600, 400);
        JFrame frame = new JFrame("Page table");
        frame.setLocation(300, 400);
        frame.setPreferredSize(dim);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        int frames, pointer = 0, hit = 0, fault = 0;
        boolean isFull = false;
        String[] buffer;//현재 프레임에 들어갈 정보 저장
        ArrayList<String> arrayList = new ArrayList<>();//값이 들어간 순서를 참조하기 위한 리스트
        String[] reference;//reference string
        String[][] memorytable;//frame

        frames = Integer.parseInt(JOptionPane.showInputDialog("프레임의 수를 입력하세요"));
        String refString = JOptionPane.showInputDialog("Enter Reference String:");
        reference = refString.split("");//한글자씩 끊어서 reference에 저장
        System.out.println();

        memorytable = new String[frames][reference.length];//프레임
        buffer = new String[frames];
        for (int j = 0; j < frames; j++)//초기 버퍼값 -1로 설정
            buffer[j] = String.valueOf(-1);

        for (int i = 0; i < reference.length; i++) {
            if (arrayList.contains(reference[i])) {//리스트에 있는 값이면 제거
                arrayList.remove(reference[i]);
            }
            arrayList.add(reference[i]);//리스트에 추가
            int search = -1;
            for (int j = 0; j < frames; j++) {//hit난 경우
                if (Objects.equals(buffer[j], reference[i])) {
                    search = j;
                    hit++;
                    break;
                }
            }
            if (search == -1) {
                if (isFull) {//프레임이 가득찬 경우
                    int min = reference.length;
                    for (int j = 0; j < frames; j++) {
                        if (arrayList.contains(buffer[j])) {//j번째 버퍼의 값이 리스트에 있는 경우
                            int temp = arrayList.indexOf(buffer[j]);//리스트의 인덱스 값을 temp에 저장
                            if (temp < min) {//temp가 작은 경우
                                min = temp;//최소값 변경
                                pointer = j;//버퍼의 인덱스값 j로 변경
                            }
                        }
                    }
                }

                buffer[pointer] = reference[i];//버퍼 수정
                fault++;
                pointer++;
                if (pointer == frames) {//버퍼의 인덱스값이 프레임 개수와 같은 경우
                    pointer = 0;//0으로 초기화
                    isFull = true;//가득찼다고 설정
                }
            }
            for (int j = 0; j < frames; j++)//버퍼의 내용 덮어씌우기
                memorytable[j][i] = buffer[j];
        }
        JTable table = new JTable(memorytable, reference);//표로 표현하기 위한 JTable 객체 생성
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < reference.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(20);
        }
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        // DefaultTableCellHeaderRenderer 생성 (가운데 정렬을 위한)
        DefaultTableCellRenderer tScheduleCellRenderer = new DefaultTableCellRenderer();
        // DefaultTableCellHeaderRenderer의 정렬을 가운데 정렬로 지정
        tScheduleCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        // 정렬할 테이블의 ColumnModel을 가져옴
        TableColumnModel tcmSchedule = table.getColumnModel();
        // 반복문을 이용하여 테이블을 가운데 정렬로 지정
        for (int i = 0; i < tcmSchedule.getColumnCount(); i++) {
            tcmSchedule.getColumn(i).setCellRenderer(tScheduleCellRenderer);
        }

        JTextArea Hit = new JTextArea("Hit의 수 : " + (hit));
        JTextArea Hitratio = new JTextArea("히트율 :" + (float) ((float) hit / reference.length) * 100 + "%");
        JTextArea Page_Fault = new JTextArea("Page Fault의 수 : " + fault);
        Hit.setLocation(100, 100);
        Hitratio.setLocation(100, 200);
        Page_Fault.setLocation(100, 300);
        JPanel panel = new JPanel();
        panel.add(Hit);
        panel.add(Hitratio);
        panel.add(Page_Fault);
        JButton backMain = new JButton("돌아가기");//main으로 돌아가기
        backMain.addActionListener(e -> {
            frame.dispose();
            main(null);
        });

        frame.add(scrollPane, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.WEST);
        frame.add(backMain,BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    public static void Second_Chance()  {
        Dimension dim = new Dimension(600, 400);
        JFrame frame = new JFrame("Page table");
        frame.setLocation(300, 400);
        frame.setPreferredSize(dim);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        int frames, pointer = 0, hit = 0, fault = 0, ref_len;
        String[][] buffer;//현재 프레임에 들어갈 정보와 refrence bit 저장
        String[] reference;//reference string
        String[][] memorytable;//frame

        frames = Integer.parseInt(JOptionPane.showInputDialog("프레임의 수를 입력하세요"));
        String refString = JOptionPane.showInputDialog("Enter Reference String:");

        reference = refString.split("");//한글자씩 끊어서 reference에 저장
        System.out.println();
        ref_len = reference.length;

        memorytable = new String[frames][ref_len];//프레임
        buffer = new String[frames][2];
        for (int j = 0; j < frames; j++) {//초기 버퍼값 설정
            buffer[j][0] = String.valueOf(-1);
            buffer[j][1] = String.valueOf(-1);
        }

        for (int i = 0; i < ref_len; i++) {//reference string의 길이만큼 반복
            int search = -1;
            for (int j = 0; j < frames; j++) {//hit난 경우
                if (Objects.equals(buffer[j][0], reference[i])) {
                    search = j;
                    hit++;
                    buffer[j][1] = String.valueOf(1);//reference bit도 1로 다시 설정
                    break;
                }
            }
            if (search == -1) {
                while (Integer.parseInt(buffer[pointer][1]) == 1) {//reference bit가 1인 경우
                    buffer[pointer][1] = String.valueOf(0);//reference bit를 0으로 설정하고
                    pointer++;//다음 버퍼의 값으로 간다.
                    if (pointer == frames)//버퍼의 인덱스값이 프레임 개수와 같은 경우
                        pointer = 0;//0으로 초기화
                }//reference bit가 0인 pointer를 찾을때까지 반복
                buffer[pointer][0] = reference[i];//버퍼 수정
                buffer[pointer][1] = String.valueOf(1);//reference bit 1로 설정
                fault++;
                pointer++;
                if (pointer == frames) pointer = 0;
            }
            for (int j = 0; j < frames; j++) {//버퍼의 내용 덮어씌우기
                String tempValue = buffer[j][0] + " | " + buffer[j][1];//memorytable에 reference bit 값을 넣기 위한 문자열 변환
                memorytable[j][i] = tempValue;
            }
        }

        JTable table = new JTable(memorytable, reference);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < reference.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(40);
        }
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        // DefaultTableCellHeaderRenderer 생성 (가운데 정렬을 위한)
        DefaultTableCellRenderer tScheduleCellRenderer = new DefaultTableCellRenderer();
        // DefaultTableCellHeaderRenderer의 정렬을 가운데 정렬로 지정
        tScheduleCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        // 정렬할 테이블의 ColumnModel을 가져옴
        TableColumnModel tcmSchedule = table.getColumnModel();
        // 반복문을 이용하여 테이블을 가운데 정렬로 지정
        for (int i = 0; i < tcmSchedule.getColumnCount(); i++) {
            tcmSchedule.getColumn(i).setCellRenderer(tScheduleCellRenderer);
        }

        JTextArea Hit = new JTextArea("Hit의 수 : " + (hit));
        JTextArea Hitratio = new JTextArea("히트율 :" + (float) ((float) hit / reference.length) * 100 + "%");
        JTextArea Page_Fault = new JTextArea("Page Fault의 수 : " + fault);
        Hit.setLocation(100, 100);
        Hitratio.setLocation(100, 200);
        Page_Fault.setLocation(100, 300);
        JPanel panel = new JPanel();
        panel.add(Hit);
        panel.add(Hitratio);
        panel.add(Page_Fault);

        JButton backMain = new JButton("돌아가기");//main으로 돌아가기
        backMain.addActionListener(e -> {
            frame.dispose();
            main(null);
        });

        frame.add(scrollPane, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.WEST);
        frame.add(backMain,BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    public static void Second_Chance_with_LRU() {
        Dimension dim = new Dimension(600, 400);
        JFrame frame = new JFrame("Page table");
        frame.setLocation(300, 400);
        frame.setPreferredSize(dim);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        int frames, pointer = 0, hit = 0, fault = 0, ref_len;
        String[][] buffer;//현재 프레임에 들어갈 정보와 refrence bit 저장
        String[] reference;//reference string
        String[][] memorytable;//frame
        ArrayList<String> arrayList = new ArrayList<>();//값이 들어간 순서를 참조하기 위한 리스트

        frames = Integer.parseInt(JOptionPane.showInputDialog("프레임의 수를 입력하세요"));
        String refString = JOptionPane.showInputDialog("Enter Reference String:");
        reference = refString.split("");//한글자씩 끊어서 reference에 저장
        ref_len = reference.length;

        memorytable = new String[frames][ref_len];//프레임
        buffer = new String[frames][2];
        for (int j = 0; j < frames; j++) {//초기 버퍼값 설정
            buffer[j][0] = String.valueOf(-1);
            buffer[j][1] = String.valueOf(-1);
        }

        for (int i = 0; i < ref_len; i++) {//reference string의 길이만큼 반복
            if (arrayList.contains(reference[i])) {//리스트에 있는 값이면 제거
                arrayList.remove(reference[i]);
            }
            arrayList.add(reference[i]);//리스트에 추가
            int search = -1;
            for (int j = 0; j < frames; j++) {//hit난 경우
                if (Objects.equals(buffer[j][0], reference[i])) {
                    search = j;
                    hit++;
                    buffer[j][1] = String.valueOf(1);//reference bit도 1로 다시 설정
                    break;
                }
            }
            if (search == -1) {
                while (Integer.parseInt(buffer[pointer][1]) == 1) {//reference bit가 1인 경우
                    buffer[pointer][1] = String.valueOf(0);//reference bit를 0으로 설정하고
                    pointer++;//다음 버퍼의 값으로 간다.
                    if (pointer == frames)//버퍼의 인덱스값이 프레임 개수와 같은 경우
                        pointer = 0;//0으로 초기화
                }
                int count = 0;
                for (int j = 0; j < frames; j++) {//reference bit가 0인 페이지 개수 찾기
                    if (Integer.parseInt(buffer[j][1]) == 0) {
                        count++;
                    }
                }
                int min = reference.length;
                if (count >= 2) {//0인 값이 2개 이상인 경우
                    for (int j = 0; j < frames; j++) {//프레임 수 만큼 반복하여 리스트의 인덱스 값이 가장 작은 것을 찾는다.
                        if (arrayList.contains(buffer[j][0]) && Integer.parseInt(buffer[j][1]) == 0) {//j번째 버퍼의 값이 리스트에 있고 reference bit가 0인 경우
                            int temp = arrayList.indexOf(buffer[j][0]);//리스트의 인덱스 값을 temp에 저장
                            if (temp < min) {//temp가 작은 경우
                                min = temp;//최소값 변경
                                pointer = j;//버퍼의 인덱스값 j로 변경
                            }
                        }
                    }
                }
                buffer[pointer][0] = reference[i];//버퍼 수정
                buffer[pointer][1] = String.valueOf(1);//reference bit 1로 설정
                fault++;
                pointer++;
                if (pointer == frames) pointer = 0;
            }
            for (int j = 0; j < frames; j++) {//버퍼의 내용 덮어씌우기
                String tempValue = buffer[j][0] + " | " + buffer[j][1];//memorytable에 reference bit 값을 넣기 위한 문자열 변환
                memorytable[j][i] = tempValue;
            }
        }
        JTable table = new JTable(memorytable, reference);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < reference.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(40);
        }
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        // DefaultTableCellHeaderRenderer 생성 (가운데 정렬을 위한)
        DefaultTableCellRenderer tScheduleCellRenderer = new DefaultTableCellRenderer();
        // DefaultTableCellHeaderRenderer의 정렬을 가운데 정렬로 지정
        tScheduleCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        // 정렬할 테이블의 ColumnModel을 가져옴
        TableColumnModel tcmSchedule = table.getColumnModel();
        // 반복문을 이용하여 테이블을 가운데 정렬로 지정
        for (int i = 0; i < tcmSchedule.getColumnCount(); i++) {
            tcmSchedule.getColumn(i).setCellRenderer(tScheduleCellRenderer);
        }

        JTextArea Hit = new JTextArea("Hit의 수 : " + (hit));
        JTextArea Hitratio = new JTextArea("히트율 :" + (float) ((float) hit / reference.length) * 100 + "%");
        JTextArea Page_Fault = new JTextArea("Page Fault의 수 : " + fault);
        Hit.setLocation(100, 100);
        Hitratio.setLocation(100, 200);
        Page_Fault.setLocation(100, 300);
        JPanel panel = new JPanel();
        panel.add(Hit);
        panel.add(Hitratio);
        panel.add(Page_Fault);

        JButton backMain = new JButton("돌아가기");//main으로 돌아가기
        backMain.addActionListener(e -> {
            frame.dispose();
            main(null);
        });

        frame.add(scrollPane, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.WEST);
        frame.add(backMain,BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    public static void LFU() {
        Dimension dim = new Dimension(600, 400);
        JFrame frame = new JFrame("Page table");
        frame.setLocation(300, 400);
        frame.setPreferredSize(dim);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        int frames, pointer = 0, hit = 0, fault = 0;
        boolean isFull = false;
        String[] buffer;//현재 프레임에 들어갈 정보 저장
        ArrayList<String> arrayList = new ArrayList<>();//값이 들어간 순서를 참조하기 위한 리스트
        HashMap<String, Integer> mostused = new HashMap<>();
        String[] reference;//reference string
        String[][] memorytable;//frame

        frames = Integer.parseInt(JOptionPane.showInputDialog("프레임의 수를 입력하세요"));
        String refString = JOptionPane.showInputDialog("Enter Reference String:");
        reference = refString.split("");//한글자씩 끊어서 reference에 저장

        memorytable = new String[frames][reference.length];//프레임
        buffer = new String[frames];
        for (int j = 0; j < frames; j++)//초기 버퍼값 -1로 설정
            buffer[j] = String.valueOf(-1);

        for (int i = 0; i < reference.length; i++) {

            if (arrayList.contains(reference[i])) {//리스트에 있는 값이면 제거
                arrayList.remove(reference[i]);
            }
            arrayList.add(reference[i]);//리스트에 추가
            mostused.put(reference[i], mostused.containsKey(reference[i]) ? mostused.get(reference[i]) + 1 : 1);//들어온 페이지를 map에 넣고 있는값이면 value를 1을 증가시킨다
            int search = -1;
            for (int j = 0; j < frames; j++) {//hit난 경우
                if (Objects.equals(buffer[j], reference[i])) {
                    search = j;
                    hit++;
                    break;
                }
            }
            int temp = reference.length;
            int current_highest = 0;
            if (search == -1) {
                if (isFull) {//프레임이 가득찬 경우
                    int min = reference.length;
                    for (int j = 0; j < frames; j++) {
                        if (mostused.containsKey(buffer[j])) {//들어온 페이지가 이전에 들어온적이 있는 경우
                            temp = mostused.get(buffer[j]);
                        }
                        if (temp > current_highest) {//현재 상황에 이전에 가장 많이 들어온 값을 찾아 저장한다.
                            current_highest = temp;
                        }
                    }
                    for (int j = 0; j < frames; j++) {
                        if (mostused.containsKey(buffer[j])) {//j번째 버퍼의 값이 이전에 나온 적이 있는 경우
                            temp = mostused.get(buffer[j]);//리스트의 인덱스 값을 temp에 저장
                            if (temp == min) {//이전에 나온 횟수가 같은 경우
                                int lrumin = reference.length;
                                for (j = 0; j < frames; j++) {//LRU를 적용
                                    if (arrayList.contains(buffer[j]) && mostused.get(buffer[j]) < current_highest) {//j번째 버퍼의 값이 리스트에 있고 current_highest보다 작은 경우
                                        int lrutemp = arrayList.indexOf(buffer[j]);//리스트의 인덱스 값을 lrutemp에 저장
                                        if (lrutemp < lrumin) {//lrutemp가 작은 경우
                                            lrumin = lrutemp;//최소값 변경
                                            pointer = j;//버퍼의 인덱스값 j로 변경
                                        }
                                    }
                                }
                            }
                            if (temp < min) {//temp가 작은 경우
                                min = temp;//최소값 변경
                                pointer = j;//버퍼의 인덱스값 j로 변경
                            }
                        }
                    }
                }
                buffer[pointer] = reference[i];//버퍼 수정
                fault++;
                pointer++;
                if (pointer == frames) {//버퍼의 인덱스값이 프레임 개수와 같은 경우
                    pointer = 0;//0으로 초기화
                    isFull = true;//가득찼다고 설정
                }
            }
            for (int j = 0; j < frames; j++)//버퍼의 내용 덮어씌우기
                memorytable[j][i] = buffer[j];
        }
        JTable table = new JTable(memorytable, reference);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < reference.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(20);
        }
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        // DefaultTableCellHeaderRenderer 생성 (가운데 정렬을 위한)
        DefaultTableCellRenderer tScheduleCellRenderer = new DefaultTableCellRenderer();
        // DefaultTableCellHeaderRenderer의 정렬을 가운데 정렬로 지정
        tScheduleCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        // 정렬할 테이블의 ColumnModel을 가져옴
        TableColumnModel tcmSchedule = table.getColumnModel();
        // 반복문을 이용하여 테이블을 가운데 정렬로 지정
        for (int i = 0; i < tcmSchedule.getColumnCount(); i++) {
            tcmSchedule.getColumn(i).setCellRenderer(tScheduleCellRenderer);
        }

        JTextArea Hit = new JTextArea("Hit의 수 : " + (hit));
        JTextArea Hitratio = new JTextArea("히트율 :" + (float) ((float) hit / reference.length) * 100 + "%");
        JTextArea Page_Fault = new JTextArea("Page Fault의 수 : " + fault);
        Hit.setLocation(100, 100);
        Hitratio.setLocation(100, 200);
        Page_Fault.setLocation(100, 300);
        JPanel panel = new JPanel();
        panel.add(Hit);
        panel.add(Hitratio);
        panel.add(Page_Fault);

        JButton backMain = new JButton("돌아가기");//main으로 돌아가기
        backMain.addActionListener(e -> {
            frame.dispose();
            main(null);
        });

        frame.add(scrollPane, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.WEST);
        frame.add(backMain,BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }
}
