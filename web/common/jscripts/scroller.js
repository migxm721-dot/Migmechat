function scrollUp(scroller_height, scroll_step)
{
  var container_height = parseInt(document.getElementById("scroll_container").style.height);
  var content = document.getElementById("scroll_content");
  var content_height = parseInt(content.offsetHeight);
  var content_top = parseInt(content.style.top);

  if (content_top < 0)
  {
    content_top = content_top + scroll_step;
    if (content_top > 0) content_top = 0;
    content.style.top = content_top + "px";
    content_top = parseInt(content.style.top);

    var scroll_top = ((scroller_height * (0-content_top))/(content_height - container_height));
    if (scroll_top < 0) scroll_top = 0;

    document.getElementById("scroller").style.top = scroll_top + "px"
  }

  return true;
}

function scrollDown(scroller_height, scroll_step)
{
  var container_height = parseInt(document.getElementById("scroll_container").style.height);
  var content = document.getElementById("scroll_content");
  var content_height = parseInt(content.offsetHeight);
  var content_top = parseInt(content.style.top);

  if (content_top > container_height - content_height)
  {
    content_top = content_top - scroll_step;
    if (content_top < container_height - content_height) content_top = container_height - content_height;
    content.style.top = content_top + "px";
    content_top = parseInt(content.style.top);

    var scroll_top = ((scroller_height * (0-content_top))/(content_height - container_height));
    if (scroll_top > scroller_height) scroll_top = scroller_height;
    document.getElementById("scroller").style.top = scroll_top + "px"
  }

  return true;
}

function scrollMembersLeft()
{
  var container_width = parseInt(document.getElementById("members_block").style.width);
  var content = document.getElementById("members_table");
  var content_width = parseInt(content.offsetWidth);
  var content_left = parseInt(content.style.left);

  if (content_left < 0)
  {
    content_left = content_left + 65;
    if (content_left > 0) content_left = 0;
    content.style.left = content_left + "px";
    content_left = parseInt(content.style.left);

    var scroll_left = ((577 * (0-content_left))/(content_width - container_width));
    if (scroll_left < 0) scroll_left = 0;

    document.getElementById("scroller").style.left = scroll_left + "px"
  }

  return true;
}

function scrollMembersRight()
{
  var container_width = parseInt(document.getElementById("members_block").style.width);
  var content = document.getElementById("members_table");
  var content_width = parseInt(content.offsetWidth);
  var content_left = parseInt(content.style.left);

  if (content_left > container_width - content_width)
  {
    content_left = content_left - 65;
    if (content_left < container_width - content_width) content_left = container_width - content_width;
    content.style.left = content_left + "px";
    content_left = parseInt(content.style.left);

    var scroll_left = ((577 * (0-content_left))/(content_width - container_width));
    if (scroll_left > 577) scroll_left = 577;
    document.getElementById("scroller").style.left = scroll_left + "px"
  }

  return true;
}