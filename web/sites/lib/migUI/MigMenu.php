<?php

class MigMenu extends MigUi {
    const DEFAULT_TITLE_CLASS = 'heading2';

    const SIMPLE = 0;
    const SCROLL = 1;

    private $icon_left = "/sites/resources/images/touch-blue/misc/icon_arrow_left_7.png";
    private $icon_right = "/sites/resources/images/touch-blue/misc/icon_arrow_right_7.png";
    private $menu_items = array();

    private $type = self::SIMPLE;
    private $title;

    public function display() {

        if (empty($this->menu_items)) {
//            echo "Scrollable Menu is Empty";
            return;
        }

        if (!empty($this->title->contents)) {
            $result = $this->title->generate();
        }

        switch ($this->type) {
            case self::SCROLL:
                $result .= $this->generateScroll();
                break;
            case self::SIMPLE:
                $result .= $this->generateSimple();
                break;
        }

        echo($result);
    }

    private function generateScroll() {

        $result =
                '<div class="scroll-menu-wrapper" usedWidth="0">
                <div class="scroll-menu-container">
        ';

        $result .= $this->getMenuItems();
        $result .= '
                </div>
            </div>
        ';

        return $result;
    }

    /**
     *
     * @return <String>
     */
    private function generateSimple() {

        $result = '<div style="text-align: center">';
        $result .= $this->getMenuItems();
        $result .= '</div>';

        return $result;
    }

    public function setTitle($title) {
        if ($title instanceof MigItem) {
            $this->title = $title;
        } else if (is_string($title)) {
            $this->title = new MigItem(MigItem::DIV);
            $this->title->addClass(self::DEFAULT_TITLE_CLASS);
            $this->title->contents = $title;
        } else {
            throw new Exception('Invalid Title MigListView Type: ' . $title
                    . ' type is ' . gettype($title) . ' should be MigItem or String');
        }
    }

    public function addMenuItem($item) {
        if ($item instanceof MigMenuItem) {
            $this->menu_items[] = $item;
        } else {
            throw new Exception('Invalid type of ScrollableMenu Item: '
                    . $item . ' is type ' . gettype($item) . ' should be MigMenuItem');
        }
    }

    private function getMenuItems() {
        $result = '
			<div class="scroll-menu-items">';
        foreach ($this->menu_items as $item) {
            $result .= $item->generate();
        }
        $result .= "</div>";
        return $result;
    }

}

?>