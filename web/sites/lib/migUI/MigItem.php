<?php

class MigItem {

    const NONE = 0;
    const DIV = 1;
    const URL = 2;
    const IMAGE = 3;
    const SPAN = 4;

    public $type = self::DIV;
    public $name = '';
    public $id = '';
    public $href = '';
    public $contents = '';
    public $alt = '';
    private $classes = array();
    private $style = '';

    /**
     * Item Constructor
     *
     * @param <type> $type
     */
    function MigItem($type) {
        $this->type = $type;
    }

    public function addClass($class) {
        $this->classes[] = $class;
    }

    public function generate() {
        switch ($this->type) {
            case self::DIV:
                return self::generateDivOrSpan('div');
            case self::URL:
                return self::generateURL();
            case self::IMAGE:
                return self::generateIMG();
            case self::SPAN:
                return self::generateDivOrSpan('span');
            default :
                return $this->contents;
        }
    }

    public function setStyle($style) {
        $this->style = $style;
    }

    /**
     * Generate a div tag
     */
    private function generateDivOrSpan($type) {
        $result = '<' . $type;

        if (!empty($this->classes)) {
            $result .= ' ' . self::generateClasses() . ' ';
        }
        if (!empty($this->id)) {
            $result .= ' id="' . $this->id . '" ';
        }
        if (!empty($this->style)) {
            $result .= ' style="' . $this->style . '" ';
        }
        $result .= '>';
        if (!empty($this->contents)) {
            $result .= $this->contents;
        }
        $result .= '</'.$type.'>';

        return $result;
    }

    /**
     * Generate a URL Tag
     */
    private function generateURL() {
        $result = '<a';

        if (!empty($this->classes)) {
            $result .= ' ' . self::generateClasses() . ' ';
        }
        if (!empty($this->id)) {
            $result .= ' id="' . $this->id . '" ';
        }
        if (!empty($this->style)) {
            $result .= ' style="' . $this->style . '" ';
        }
        $result .= ' href="' . $this->href . '"> ';
        if (!empty($this->contents)) {
            $result .= $this->contents;
        }
        $result .= '</a>';

        return $result;
    }

    private function generateIMG() {
        $result = '<img src="' . $this->href . '"';

        if (!empty($this->classes)) {
            $result .= ' ' . self::generateClasses() . ' ';
        }
        if (!empty($this->id)) {
            $result .= ' id="' . $this->id . '" ';
        }
        if (!empty($this->style)) {
            $result .= ' style="' . $this->style . '" ';
        }
        if (!empty($this->alt)) {
            $result .= ' alt="' . $this->alt .'" ';
        }
        $result .= '/>';

        return $result;
    }

    private function generateClasses() {
        $result .= 'class="';
        $ctr = count($this->classes);
        foreach ($this->classes as $value) {
            $result .= $value;
            $ctr--;
            if ($ctr >= 1) {
                $result .= ' ';
            }
        }
        $result .= '"';

        return $result;
    }

}

?>