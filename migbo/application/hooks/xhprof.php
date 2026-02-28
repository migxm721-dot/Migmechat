<?php
class XHProf {
   private $name = 'miniBlog';
   private $xhprof_config = false;
   private static $ran = false;

   public function __construct()
   {
      // XHProf Config
      include APPPATH . 'config/' . ENVIRONMENT . '/urls.php';
      $this->xhprof_config = $config['xhprof'];
      if(!function_exists('xhprof_enable'))
         $this->xhprof_config['enabled'] = false;
   }

   public function xhprof_start()
   {
      if($this->xhprof_config['enabled'])
      {
         if(mt_rand(1, $this->xhprof_config['sample']) == 1)
         {
            // include_once PACKAGESPATH . 'xhprof/libraries/utils/xhprof_lib.php';
            // include_once PACKAGESPATH . 'xhprof/libraries/utils/xhprof_runs.php';
            include_once PACKAGESPATH . 'xhprof/libraries/utils/xhprof_runs_mig33.php';
            xhprof_enable(XHPROF_FLAGS_NO_BUILTINS + XHPROF_FLAGS_CPU + XHPROF_FLAGS_MEMORY);
            self::$ran = true;
         }
      }
   }

   public function xhprof_end()
   {
      if($this->xhprof_config['enabled'])
      {
         if(self::$ran)
         {
            $xhprof_data = xhprof_disable();
            // $xhprof_run = new XHProfRuns_Default();
            $xhprof_run = new XHProfRuns_Mig33($this->name);
            $xhprof_run->save_run($xhprof_data, $this->name);
         }
      }
   }
}
?>