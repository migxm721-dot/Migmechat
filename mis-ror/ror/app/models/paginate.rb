class Paginate

    @Page
    @RecordsPerPage
    @Link
    @TotalRecords
    
    attr_accessor :Page, :RecordsPerPage, :Link, :TotalRecords
    
    def initialize(args)
        @Page = args[:Page].to_i || 1
        @RecordsPerPage = args[:RecordsPerPage].to_i || 30
        @Link = args[:Link] || ''
        @TotalRecords = args[:TotalRecords] || 0
    end
    
    def startRow
        return (@Page * @RecordsPerPage) - @RecordsPerPage
    end
    
    def Prev
        return @Page - 1 unless @Page - 1 <= 0
    end
    
    def Next
        return 1 if @TotalRecords == 0
        return @Page + 1 unless @Page >= (@TotalRecords/@RecordsPerPage) + 1
    end
    
end