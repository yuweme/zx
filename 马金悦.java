public class LibraryTest implements ActionListener
{
private String searchAllsql = "select * from books";
	private String searchonesql_book_id = "select * from books where book_id = ?";
	private String searchonesql_book_name = "select * from books where book_name = ?";
	private String searchonesql_price = "select * from books where price = ?";
	private String searchonesql_author = "select * from books where author = ?";
	private String searchonesql_publisher = "select * from books where publisher = ?";
	private String deletesql = "delete from books where book_name = ?";
	private String addSql = "insert into books values (null,?,?,?,?,?)";
	private String changeSql = "update books set book_name = ?, price = ?,author = ?, publisher = ? where book_id = ?";

	private String url, account, password;
	private Connection conn;
	private Statement stmt;
	public LibraryTest() 
	{
		try{
			//这里初始化数据库
			//输入一下数据
			Properties prop = new Properties();
			prop.load(new FileInputStream("E:/test/mysql.ini"));
			url = prop.getProperty("url");
			account = prop.getProperty("account");
			password = prop.getProperty("password");
			//数据库
			conn = DriverManager.getConnection(url, account, password);
			stmt = conn.createStatement();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	//界面初始化
	private JFrame mainWin = new JFrame("图书管理程序");
	private JScrollPane scrollPane; //包装table的滚动条
	private JTable table;//表格
	private MyMouseListener myMouseListener = new MyMouseListener();
	private JTable inputTable ;//用于添加的表格
	private JLabel showCover = new JLabel();//显示封面
	//搜索输入标题框
	private JTextField bookTitle;
	//搜索框中的hint
	String hint = "输入书名（用于查询、删除、修改）：";

	//添加封面用的路径显示
	JTextField coverPath;
		
	//增加一个查询条件选项
	JComboBox<String> searchCondition;	
	String condition;
	
	public void initWindow() throws Exception
	{
		//控制按钮
		JButton search = new JButton("查询");
		JButton additem = new JButton("添加");
		JButton deleteitem = new JButton("删除");
		JButton changeitem = new JButton("修改");
		
		JPanel leftControl = new JPanel(new FlowLayout());
		leftControl.add(search);
		leftControl.add(additem);
		leftControl.add(deleteitem);
		leftControl.add(changeitem);

		//按钮设置点击事件
		search.addActionListener(this);
		additem.addActionListener(this);
		deleteitem.addActionListener(this);
		changeitem.addActionListener(this);
		

		//查询框
		JPanel searchBook = new JPanel();
		//一个选择按什么查询的JComboBox
		
		ResultSet rs = stmt.executeQuery(searchAllsql);
		ResultSetMetaData rsmd= rs.getMetaData();
		int columnCount = rsmd.getColumnCount()-1;
		Vector<String> conditions = new Vector<>();
		for( int i =0 ; i < columnCount ; i++)
		{
			conditions.add(rsmd.getColumnName(i+1));
			if( i == 0)
				condition = rsmd.getColumnName(i+1);//给condition设置默认值
		}
		searchCondition = new JComboBox(conditions);
	
		searchCondition.addItemListener(event ->
			{
				condition = searchCondition.getSelectedItem().toString();
				mainWin.validate();
			});
	
		JLabel searchLabel = new JLabel(hint);
		bookTitle = new JTextField(50);
		bookTitle.addFocusListener(new TextFocusListener(bookTitle,hint));
		searchBook.add(searchCondition, BorderLayout.WEST);
		searchBook.add(bookTitle, BorderLayout.CENTER);
		searchBook.add(leftControl,BorderLayout.SOUTH);
		searchBook.setPreferredSize(new Dimension(50,100));
		mainWin.add(searchBook,BorderLayout.NORTH);
		
		//显示表格初始化
		updateTableAll();
	
		//给table添加鼠标事件
		table.addMouseListener( myMouseListener);

		//窗口下部用于添加记录用的表格初始化
		
			
		Vector<String> columnNames = new Vector<>();
		Vector<Vector<String>> data = new Vector<>();
		Vector<String> onerow = new Vector<>();
		for( int i= 1; i < columnCount;i++)
		{
			columnNames.add(rsmd.getColumnName(i+1));
			onerow.add("···");
		}
		data.add(onerow);
		inputTable = new JTable(data,columnNames);
			
		JScrollPane inputScroll = new JScrollPane(inputTable);
		inputScroll.setPreferredSize(new Dimension(500,40));
		JPanel jp = new JPanel(new GridLayout(4,1));