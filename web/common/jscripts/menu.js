function showhideMenuItems(nm)
{
  var leftimg = document.getElementById('menu_left_image_' + nm);
  var rightimg = document.getElementById('menu_right_image_' + nm);
  var elem = document.getElementById('menu_' + nm);

  if (leftimg.style.display == 'block')
  {
    elem.style.color = '#606060';
    elem.style.padding = '0px 21px';
    elem.style.background = 'transparent';

    leftimg.style.display = 'none';
    rightimg.style.display = 'none';
  }
  else
  {
    elem.style.color = 'white';
    elem.style.padding = '0px 16px';
    elem.style.background = '#0FABDD';

    leftimg.style.display = 'block';
    rightimg.style.display = 'block';
  }

  return true;
}