package com.myopicmobileX.textwarrior.android;
import android.content.*;
import android.content.res.*;
import android.graphics.drawable.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.myopicmobileX.textwarrior.common.*;
import java.lang.reflect.*;
import java.util.*;
import com.mythoi.androluaj.util.*;

public class AutoCompletePanel
{

  private FreeScrollingTextField _textField;
  private Context _context;
  //原
  //private static Language _globalLanguage = LanguageNonProg.getInstance();
  //改
  private static Language _globalLanguage = LanguageLuaJava.getInstance();

  private ListPopupWindow _autoCompletePanel;
  private AutoCompletePanel.MyAdapter _adapter;
  private Filter _filter;

  private int _verticalOffset;

  private int _height;

  private int _horizontal;

  private CharSequence _constraint;

  private int _backgroundColor;

  private GradientDrawable gd;

  private int _textColor;

  public AutoCompletePanel(FreeScrollingTextField textField)
  {
    _textField = textField;
    _context = textField.getContext();
    initAutoCompletePanel();

  }

  public void setTextColor(int color)
  {
    _textColor = color;
    gd.setStroke(1, color);
    _autoCompletePanel.setBackgroundDrawable(gd);
  }


  public void setBackgroundColor(int color)
  {
    _backgroundColor = color;
    gd.setColor(color);
    _autoCompletePanel.setBackgroundDrawable(gd);
  }

  public void setBackground(Drawable color)
  {
    _autoCompletePanel.setBackgroundDrawable(color);
  }

  private void initAutoCompletePanel()
  {
    _autoCompletePanel = new ListPopupWindow(_context);
    _autoCompletePanel.setAnchorView(_textField);
    _autoCompletePanel.setAnimationStyle(-1);
    _adapter = new MyAdapter(_context, android.R.layout.simple_list_item_1);
    _autoCompletePanel.setAdapter(_adapter);
    //_autoCompletePanel.setDropDownGravity(Gravity.BOTTOM | Gravity.LEFT);
    _filter = _adapter.getFilter();
    setHeight(300);
    setWidth(ListPopupWindow.MATCH_PARENT);
    TypedArray array = _context.getTheme().obtainStyledAttributes(new int[] { 
      android.R.attr.colorBackground, 
      android.R.attr.textColorPrimary, 
    }); 
    int backgroundColor = array.getColor(0, 0xFF00FF); 
    System.out.println(backgroundColor);
    int textColor = array.getColor(1, 0xFF00FF); 
    array.recycle();
    gd = new GradientDrawable();
    gd.setColor(backgroundColor);
    gd.setCornerRadius(4);
    gd.setStroke(1, textColor);
    setTextColor(textColor);
    _autoCompletePanel.setBackgroundDrawable(gd);
    _autoCompletePanel.setOnItemClickListener(new OnItemClickListener(){

      @Override
      public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
      {
        // TODO: Implement this method
        if (getLanguage() == LanguageXML.getInstance())
        {
          String label=((TextView)p2).getText().toString();
          _textField.replaceText(_textField.getCaretPosition() - _constraint.length(), _constraint.length(), "<" + label + " ></" + label + ">");
          _adapter.abort();
          _textField.moveCaret(_textField.getCaretPosition() - label.length() - 4);
        }
      else
        {
          _textField.replaceText(_textField.getCaretPosition() - _constraint.length(), _constraint.length(), ((TextView)p2).getText().toString());
          _adapter.abort();

          /*自动导入，代码长的话太卡了不用
          String imports=ApiListView.RevoltHashMap.get(((TextView)p2).getText().toString().trim());
          if (imports != null && !MainActivity.edit.getText().toString().contains(imports) && MainActivity.edit.isOpenJava)
          {

            if (MainActivity.edit.getText().getRowCount() < 500)
            {
              int index =MainActivity. edit.getSelectionStart();//获取光标所在位置
              MainActivity.edit.insert(MainActivity.edit.getText().getRowOffset(1), "import " + imports + ";\n");//在光标处插入字符串
              MainActivity.edit. moveCaret(index + ("import " + imports + ";\n").length());
            }

          }
          */

          dismiss();
        }
      }
    });

  }

  public void setWidth(int width)
  {
    // TODO: Implement this method
    _autoCompletePanel.setWidth(width);
  }

  private void setHeight(int height)
  {
    // TODO: Implement this method

    if (_height != height)
    {
      _height = height;
      _autoCompletePanel.setHeight(height);
    }
  }

  private void setHorizontalOffset(int horizontal)
  {
    // TODO: Implement this method
    horizontal = Math.min(horizontal, _textField.getWidth() / 2);
    if (_horizontal != horizontal)
    {
      _horizontal = horizontal;
      _autoCompletePanel.setHorizontalOffset(horizontal);
    }
  }


  private void setVerticalOffset(int verticalOffset)
  {
    // TODO: Implement this method
    //verticalOffset=Math.min(verticalOffset,_textField.getWidth()/2);
    int max=0 - _autoCompletePanel.getHeight();
    if (verticalOffset > max)
    {
      _textField.scrollBy(0, verticalOffset - max);
      verticalOffset = max;
    }
    if (_verticalOffset != verticalOffset)
    {
      _verticalOffset = verticalOffset;
      _autoCompletePanel.setVerticalOffset(verticalOffset);
    }
  }

  public void update(CharSequence constraint)
  {
    _adapter.restart();
    _filter.filter(constraint);
  }

  public void show()
  {
    if (!_autoCompletePanel.isShowing())
    _autoCompletePanel.show();
    _autoCompletePanel.getListView().setFadingEdgeLength(0);
  }

  public void dismiss()
  {
    if (_autoCompletePanel.isShowing())
    {
      _autoCompletePanel.dismiss();
    }
  }
  synchronized public static void setLanguage(Language lang)
  {
    _globalLanguage = lang;
  }

  synchronized public static Language getLanguage()
  {
    return _globalLanguage;
  }

  /**
  * Adapter定义
  */
  class MyAdapter extends ArrayAdapter<String> implements Filterable
  {

    private int _h;
    private Flag _abort;

    private DisplayMetrics dm;

    public MyAdapter(android.content.Context context, int resource)
    {
      super(context, resource);
      _abort = new Flag();
      setNotifyOnChange(false);
      dm = context.getResources().getDisplayMetrics();

    }

    public void abort()
    {
      _abort.set();
    }


    private int dp(float n)
    {
      // TODO: Implement this method
      return (int)TypedValue.applyDimension(1, n, dm);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
      // TODO: Implement this method
      TextView view=(TextView) super.getView(position, convertView, parent);
      /*TextView view=null;
      if(convertView==null){
        view=new TextView(_context);
        view.setTextSize(16);
        view.setPadding(dp(8),dp(3),dp(8),dp(3));
      }
    else{
        view=(TextView) convertView;
      }
      view.setText(getItem(position));*/
      view.setTextColor(_textColor);
      return view;
    }



    public void restart()
    {
      // TODO: Implement this method
      _abort.clear();
    }

    public int getItemHeight()
    {
      if (_h != 0)
      return _h;

      LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      TextView item = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, null);
      item.measure(0, 0);
      _h = item.getMeasuredHeight();
      return _h;
    }/**
    * 实现自动完成的过滤算法
    */
    @Override
    public Filter getFilter()
    {
      Filter filter = new Filter() {

        /**
        * 本方法在后台线程执行，定义过滤算法
        */
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
          /*int l=constraint.length();
          int i=l;
          for(;i>0;i--){
          if(constraint.charAt(l-1)=='.')
          break;
        }
        if(i>0){
          constraint=constraint.subSequence(i,l);
        }*/

        // 此处实现过滤
        // 过滤后利用FilterResults将过滤结果返回
        ArrayList <String>buf = new ArrayList<String>();
        String keyword = String.valueOf(constraint).toLowerCase();
        String keyword_yuan=String.valueOf(constraint);//mythoi改，不转小写，
        String[] ss_yuan=keyword_yuan.split("\\.");
        String[] ss=keyword.split("\\.");

        if (ss.length == 2)
        {
          String pkg_yuan=ss_yuan[0];
          String pkg=ss[0];
          keyword = ss[1];


          //mythoi改，支持方法/字段补全-----------开始------
          String claz1=Lexer.varAndClass.get(pkg_yuan);
          String claz2=APIConfig.hashMap.get(claz1);
          if (_globalLanguage.isName(claz2))
          {

            try
            {
              Class clazz=Class.forName(claz1);
              Method mMethod[]=clazz.getMethods();
              for (Method method:mMethod)
              {
                Type type[]= method.getGenericParameterTypes();
                StringBuffer strbuff=new StringBuffer();
                for (Type tp:type)
                {
                  strbuff.append((tp + ",").replace("class ", ""));
                }
                String str=strbuff.toString();
                if (str.equals("") || str == null)
                str = " ";
                if (method.getName().toLowerCase().startsWith(keyword))
                if(!buf.contains(method.getName() + "("))
                buf.add(method.getName() + "(");
                // buf.add(Modifier.toString(method.getModifiers()) + " " + (method.getGenericReturnType() + " ").replaceAll("class ", "") + method.getName() + "(" + str.substring(0, str.length() - 1) + ")");
              }

              Field[] fields = clazz.getFields();
              for (Field field : fields)
              {
                if (field.getName().toLowerCase().startsWith(keyword))
                if(!buf.contains(field.getName()))
                buf.add(field.getName());
              }


            }
            catch (Exception e)
            {
              System.out.println(e + "-------");
            }

          }
          //静态的，补全
          if (_globalLanguage.isName(pkg_yuan))
          {

            try
            {
              String cla=APIConfig.RevoltHashMap.get(pkg_yuan);//获取全类名
              Class clazz=Class.forName(cla);
              Method mMethod[]=clazz.getMethods();
              for (Method method:mMethod)
              {
                Type type[]= method.getGenericParameterTypes();
                StringBuffer strbuff=new StringBuffer();
                for (Type tp:type)
                {
                  strbuff.append((tp + ",").replace("class ", ""));
                }
                String str=strbuff.toString();
                if (str.equals("") || str == null)
                str = " ";
                String methodType=Modifier.toString(method.getModifiers());//获取方法类型
                if (methodType.contains("static"))//判断是否为静态方法
                {
                  if (method.getName().toLowerCase().startsWith(keyword))
                  if(!buf.contains(method.getName() + "("))
                  buf.add(method.getName() + "(");
                  // buf.add(Modifier.toString(method.getModifiers()) + " " + (method.getGenericReturnType() + " ").replaceAll("class ", "") + method.getName() + "(" + str.substring(0, str.length() - 1) + ")");
                }
              }

              Field[] fields = clazz.getFields();
              for (Field field : fields)
              {
                String methodType=Modifier.toString(field.getModifiers());//获取字段类型
                if (methodType.contains("static"))//判断是否为静态方法
                {
                  if (field.getName().toLowerCase().startsWith(keyword))
                  if(!buf.contains(field.getName()))
                  buf.add(field.getName());
                }
              }

            }
            catch (Exception e)
            {
              System.out.println(e + "-------");
            }



          }


          //----------------mythoi改结束--------------


          if (_globalLanguage.isBasePackage(pkg))
          {
            String[] keywords=_globalLanguage.getBasePackage(pkg);
            for (String k:keywords)
            {
              if (k.toLowerCase().startsWith(keyword))
              buf.add(k);
            }
          }
        }
      else if (ss.length == 1)
        {
          if (keyword.charAt(keyword.length() - 1) == '.')
          {
            String pkg=keyword.substring(0, keyword.length() - 1);//点前面的分词
            String pkg_yuan=keyword_yuan.substring(0, keyword.length() - 1);//含yuan的都是mythoi改
            keyword = "";

            //mythoi改，支持方法/字段补全--------开始---
            String claz1=Lexer.varAndClass.get(pkg_yuan);//获取变量的全类名
            String claz2=APIConfig.hashMap.get(claz1);//获取对应的单类名
            if (_globalLanguage.isName(claz2))
            {

              try
              {
                Class clazz=Class.forName(claz1);
                Method mMethod[]=clazz.getMethods();
                for (Method method:mMethod)
                {
                  Type type[]= method.getGenericParameterTypes();
                  StringBuffer strbuff=new StringBuffer();
                  for (Type tp:type)
                  {
                    strbuff.append((tp + ",").replace("class ", ""));
                  }
                  String str=strbuff.toString();
                  if (str.equals("") || str == null)
                  str = " ";
                  if(!buf.contains(method.getName()))
                  buf.add(method.getName() + "(");
                  // buf.add(Modifier.toString(method.getModifiers()) + " " + (method.getGenericReturnType() + " ").replaceAll("class ", "") + method.getName() + "(" + str.substring(0, str.length() - 1) + ")");
                }

                Field[] fields = clazz.getFields();
                for (Field field : fields)
                {
                  if(!buf.contains(field.getName()))
                  buf.add(field.getName());
                }


              }
              catch (Exception e)
              {
                System.out.println(e + "-----------");
              }

            }
            //静态修饰的,补全
            System.out.println(pkg_yuan);
            if (_globalLanguage.isName(pkg_yuan))
            {//判断点前面的分词是否为isName(类)

              try
              {
                String cla=APIConfig.RevoltHashMap.get(pkg_yuan);//获取全类名
                System.out.println(cla);
                Class clazz=Class.forName(cla);
                Method mMethod[]=clazz.getMethods();
                for (Method method:mMethod)
                {
                  Type type[]= method.getGenericParameterTypes();
                  StringBuffer strbuff=new StringBuffer();
                  for (Type tp:type)
                  {

                    strbuff.append((tp + ",").replace("class ", ""));
                  }
                  String str=strbuff.toString();
                  if (str.equals("") || str == null)
                  str = " ";

                  String methodType=Modifier.toString(method.getModifiers());//获取方法类型
                  if (methodType.contains("static"))//判断是否为静态方法
                  if(!buf.contains(method.getName()))
                  buf.add(method.getName() + "(");
                  // buf.add(Modifier.toString(method.getModifiers()) + " " + (method.getGenericReturnType() + " ").replaceAll("class ", "") + method.getName() + "(" + str.substring(0, str.length() - 1) + ")");
                }

                Field[] fields = clazz.getFields();
                for (Field field : fields)
                {
                  String fieldType=Modifier.toString(field.getModifiers());//获取字段类型
                  if (fieldType.contains("static"))//判断是否为静态字段
                  if(!buf.contains(field.getName()))
                  buf.add(field.getName());
                }

              }
              catch (Exception e)
              {
                System.out.println(e + "-----------");
              }
            }
            //--------------------------------mythoi改结束-----+-++	



            if (_globalLanguage.isBasePackage(pkg))
            {
              String[] keywords=_globalLanguage.getBasePackage(pkg);
              for (String k:keywords)
              {
                buf.add(k);
              }
            }
          }
        else
          {

            String[] keywords = _globalLanguage.getUserWord();
            for (String k:keywords)
            {
              if (k.toLowerCase().startsWith(keyword))
              buf.add(k);
            }


            keywords = _globalLanguage.getNormalWord();
            for (String k:keywords)
            {
              if (k.toLowerCase().startsWith(keyword))
              buf.add(k);
            } 

            keywords = _globalLanguage.getKeywords();
            for (String k:keywords)
            {
              if (k.indexOf(keyword) == 0)
              buf.add(k);
            }
            keywords = _globalLanguage.getNames();
            for (String k:keywords)
            {
              if (k.toLowerCase().startsWith(keyword))
              buf.add(k);
            }



          }
        }
        _constraint = keyword;
        FilterResults filterResults = new FilterResults();
        filterResults.values = buf; // results是上面的过滤结果
        filterResults.count = buf.size(); // 结果数量
        return filterResults;
      }
      /**
      * 本方法在UI线程执行，用于更新自动完成列表
      */
      @Override
      protected void publishResults(CharSequence constraint, FilterResults results)
      {
        if (results != null && results.count > 0 && !_abort.isSet())
        {
          // 有过滤结果，显示自动完成列表
          MyAdapter.this.clear(); // 清空旧列表
          MyAdapter.this.addAll((ArrayList<String>)results.values);
          //int y = _textField.getPaintBaseline(_textField.getCaretRow()) - _textField.getScrollY();
          int y = _textField.getCaretY() + _textField.rowHeight() / 2 - _textField.getScrollY();
          setHeight(getItemHeight() * Math.min(2, results.count));
          //setHeight((int)(Math.min(_textField.getContentHeight()*0.4,getItemHeight() * Math.min(6, results.count))));

          setHorizontalOffset(_textField.getCaretX() - _textField.getScrollX());
          setVerticalOffset(y - _textField.getHeight());//_textField.getCaretY()-_textField.getScrollY()-_textField.getHeight());
          notifyDataSetChanged();
          show();
        }
      else
        {
          // 无过滤结果，关闭列表
          notifyDataSetInvalidated();
        }
      }

    };
    return filter;
  }
}
}

